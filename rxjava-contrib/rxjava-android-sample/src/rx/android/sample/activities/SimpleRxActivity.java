package rx.android.sample.activities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import rx.Observable;
import rx.Observable.OnSubscribe;
import rx.Observer;
import rx.Subscriber;
import rx.android.sample.model.Observers.LoggingObserver;
import rx.android.sample.util.LogUtil;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.operators.OnSubscribeFromIterable;
import rx.schedulers.Schedulers;
import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class SimpleRxActivity extends Activity {
	private static final String TAG = SimpleRxActivity.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		TextView tv = new TextView(this);
		int p = 16;
		tv.setPadding(p, p, p, p);
		tv.setText("See logcat for output");
		setContentView(tv);

		runSimpleStringObserver();
		runSimpleIntObserver();
	}

	private void runSimpleStringObserver() {
		String[] names = { "alpha", "beta", "charlie" };
		Observable
			.from(names)
			.subscribe(new Observer<String>() {
				@Override
				public void onNext(String s) {
					LogUtil.v(TAG, "Hello " + s);
				}
	
				@Override
				public void onCompleted() {
					LogUtil.v(TAG, "Complete");
				}
	
				@Override
				public void onError(Throwable e) {
					LogUtil.v(TAG, "Error: " + e);
				}
			});
	}

	private void runSimpleIntObserver() {
		Observable
			.create(intIteratorObs)
			.map(new Func1<Integer, Integer>() {
				@Override
				public Integer call(Integer i) {
					LogUtil.v(TAG, "map call, input: " + i);
					return i * 2;
				}
			})
			.subscribeOn(Schedulers.newThread())
			.observeOn(AndroidSchedulers.mainThread())
			.subscribe(new LoggingObserver<Integer>(TAG));
	}

	private final List<Integer> intList = new ArrayList<Integer>(Arrays.asList(0, 1, 2));
	private final OnSubscribeFromIterable<Integer> intIteratorObs = new OnSubscribeFromIterable<Integer>(intList);

	private final OnSubscribe<Integer> intGeneraterFunc = new OnSubscribe<Integer>() {
		@Override
		public void call(Subscriber<? super Integer> subscriber) {
			LogUtil.v(TAG, "onSubscribe()");
			try {
				int i;
				for (i = 0; i < 3; i++) {
					LogUtil.v(TAG, "Generating number: " + i);
					subscriber.onNext(i);
				}

				if (i > 0) {
					LogUtil.v(TAG, "Throwing runtime exception...");
					throw new IllegalStateException("Dummy error");
				}

				subscriber.onCompleted();
			} catch (RuntimeException e) {
				subscriber.onError(e);
			}
		}
	};
}
