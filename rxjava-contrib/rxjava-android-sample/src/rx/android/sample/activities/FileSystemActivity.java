package rx.android.sample.activities;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Observable.OnSubscribe;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.android.observables.AndroidObservable;
import rx.android.sample.R;
import rx.android.sample.util.LogUtil;
import rx.android.sample.util.ThreadUtils;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Based on: http://www.codeproject.com/Articles/52308/The-Rx-Framework-By-Example
 */
public class FileSystemActivity extends Activity {
	private static final String TAG = FileSystemActivity.class.getSimpleName();

	private Button cancelButton;
	private ListView listView;

	private Subscription subscription;
	private BaseAdapter adapter;

	private final List<String> data = new ArrayList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		subscription = AndroidObservable
				.bindActivity(this, filesObservable)
				.buffer(1000, TimeUnit.MILLISECONDS)
				.observeOn(AndroidSchedulers.mainThread()) // Current thread by default but we're being explicit.
				.subscribe(new Observer<List<String>>() {
					@Override
					public void onNext(List<String> values) {
						ThreadUtils.assertOnMain();
						LogUtil.v(TAG, "onNext, " + values.size() + " items");
						data.addAll(values);
						adapter.notifyDataSetChanged();
					}

					@Override
					public void onCompleted() {
						ThreadUtils.assertOnMain();
						LogUtil.v(TAG, "onCompleted");
						cancelButton.setEnabled(false);
					}

					@Override
					public void onError(Throwable e) {
						ThreadUtils.assertOnMain();
						LogUtil.handleException(TAG, e);
						cancelButton.setEnabled(false);
					}
				});

		adapter = new DataListAdapter();
		listView = (ListView) findViewById(android.R.id.list);
		listView.setAdapter(adapter);
		listView.setFastScrollEnabled(true);

		cancelButton = (Button) findViewById(R.id.cancel_button);
		cancelButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				subscription.unsubscribe();
				cancelButton.setEnabled(false);
			}
		});
	}
	
	@Override
	protected void onDestroy() {
		subscription.unsubscribe();
		super.onDestroy();
	}

	private final OnSubscribe<String> externalStorageFileFunc = new OnSubscribe<String>() {
		@Override
		public void call(Subscriber<? super String> subscriber) {
			ThreadUtils.assertNotOnMain();
			getAllFiles(subscriber, Environment.getExternalStorageDirectory());
			subscriber.onCompleted();
		}

		private void getAllFiles(Observer<? super String> observer, File candidateDir) {
			if ((subscription != null) && subscription.isUnsubscribed()) {
				return;
			}

			File[] files = candidateDir.listFiles();  // Returns files in a directory
			if (files == null) {
				observer.onNext(candidateDir.getAbsolutePath());

				try {
					Thread.sleep(450); // Fake a delay to simulate slower operation
				} catch (InterruptedException e) {
					LogUtil.handleException(TAG, e);
				}
			}
			else {
				for (File f : files) {
					getAllFiles(observer, f);
				}
			}
		}
	};

	private final Observable<String> filesObservable = Observable
			.create(externalStorageFileFunc)
			.subscribeOn(Schedulers.io());

	private class DataListAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return data.size();
		}

		@Override
		public String getItem(int position) {
			return data.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = new TextView(FileSystemActivity.this);
			}
			((TextView)convertView).setText(getItem(position));
			return convertView;
		}
	}
}
