package rx.android.sample.activities;

import rx.Observable;
import rx.Observable.OnSubscribe;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.android.sample.model.Observers.LoggingObserver;
import rx.android.sample.util.IOUtils;
import rx.android.sample.util.LogUtil;
import rx.android.sample.util.ThreadUtils;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class DownloadActivity extends Activity {
	protected static final String TAG = DownloadActivity.class.getSimpleName();

	private TextView tv;

	private Subscription subscription;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		int p = 48;

		tv = new TextView(this);
		tv.setPadding(p, p, p, p);
		tv.setText("Tap here to start download");
		tv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				tv.setEnabled(false);

				subscription = createDownloadObservable()
					.subscribe(new Observer<String>() {
						@Override
						public void onNext(String val) {
							LogUtil.v(TAG, "onNext: " + val);
						}

						@Override
						public void onCompleted() {
							LogUtil.v(TAG, "onCompleted");
							tv.setEnabled(true);
						}

						@Override
						public void onError(Throwable e) {
							LogUtil.handleException(TAG, e);
							tv.setEnabled(true);
						}
					});
			}
		});

//		subscription = ViewObservable
//			.clicks(tv, false)
//			.map(new Func1<View, View>() {
//				@Override
//				public View call(final View view) {
//					LogUtil.v(TAG, "Map, view: " + view);
//
//					view.setEnabled(false);
//					createDownloadObservable()
//						.subscribe(new Observer<String>() {
//							@Override
//							public void onNext(String val) {
//								LogUtil.v(TAG, "onNext: " + val);
//							}
//	
//							@Override
//							public void onCompleted() {
//								LogUtil.v(TAG, "onCompleted");
//								view.setEnabled(true);
//							}
//	
//							@Override
//							public void onError(Throwable e) {
//								LogUtil.handleException(TAG, e);
//								view.setEnabled(true);
//							}
//						});
//					return view;
//				}
//			})
//			.subscribe();

//		ViewObservable
//			.clicks(tv, false)
//			.map(new Func1<View, View>() {
//				@Override
//				public View call(final View view) {
//					view.setEnabled(false);
//					subscription = createDownloadObservable()
//						.subscribe(new EnableViewObserver<String>(TAG, tv));
//					return view;
//				}
//			})
//			.subscribe();

		setContentView(tv);
	}

	public Observable<String> createDownloadObservable() {
	    return Observable.create(new OnSubscribe<String>() {
			@Override
			public void call(Subscriber<? super String> subscriber) {
	        	ThreadUtils.assertNotOnMain();
	            try {
	            	LogUtil.v(TAG, "Starting http operation");
	            	Thread.sleep(3000);
	            	subscriber.onNext(IOUtils.fetchResponse("http://en.wikipedia.org/wiki/tiger"));
	            	subscriber.onCompleted();
	            } catch (Exception e) {
	            	subscriber.onError(e);
	            }
			}
	    })
		.subscribeOn(Schedulers.io())
		.observeOn(AndroidSchedulers.mainThread());
	}

	@Override
	protected void onDestroy() {
		if (subscription != null) {
			LogUtil.v(TAG, "Unsubscribing...");
			subscription.unsubscribe();
		}
		super.onDestroy();
	}

	class EnableViewObserver<T> extends LoggingObserver<T> {

		public EnableViewObserver(String logTag) {
			super(logTag);
		}

		@Override
		public void onCompleted() {
			super.onCompleted();
			tv.setEnabled(true);
		}

		@Override
		public void onError(Throwable e) {
			super.onError(e);
			tv.setEnabled(true);
		}
	}
}
