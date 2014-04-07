package rx.android.sample.activities;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import retrofit.RestAdapter;
import retrofit.http.GET;
import retrofit.http.Query;
import rx.Observable;
import rx.Subscription;
import rx.android.sample.R;
import rx.android.sample.model.Observers;
import rx.android.sample.model.WeatherData;
import rx.android.sample.util.LogUtil;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import android.app.ListActivity;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class WeatherActivity extends ListActivity {
	private static final String TAG = "WeatherActivity";

	private final Map<String, WeatherData> weather = new LinkedHashMap<String, WeatherData>();

	private ListView listView;
	private WeatherAdapter adapter;

	private Subscription subscription;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
    	LogUtil.v(TAG, "Num threads: " + Schedulers.io().degreeOfParallelism());

		listView = new ListView(this);
		listView.setId(android.R.id.list);

		adapter = new WeatherAdapter();
		setListAdapter(adapter);

		setContentView(listView);
		getWeatherForCities(Arrays.asList(
				"San Francisco", "Sydney", "London", "foobar"));
	}

	@Override
	protected void onDestroy() {
		subscription.unsubscribe();
		super.onDestroy();
	}

	private void getWeatherForCities(List<String> cities) {
		Observable<WeatherData> observable = Observable.from(cities)
	        .flatMap(new Func1<String, Observable<WeatherData>>() {
	            @Override
	            public Observable<WeatherData> call(String s) {
	                return ApiManager.getWeather(s);
	            }
	        })
	        .observeOn(AndroidSchedulers.mainThread());

		subscription = observable.subscribe(new Observers.LoggingObserver<WeatherData>(TAG) {
			@Override
			public void onNext(WeatherData data) {
				super.onNext(data);
				weather.put(data.name, data);
				adapter.notifyDataSetChanged();
			}
		});
	}

	private class WeatherAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return weather.size();
		}

		@Override
		public WeatherData getItem(int position) {
			return weather.get(weather.keySet().toArray()[position]);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			TextView tv = (TextView) convertView;
			if (tv == null) {
				tv = new TextView(WeatherActivity.this);
				Resources r = getResources();
				int p = (int)r.getDimension(R.dimen.content_padding);
				tv.setPadding(p, p, p, p);
				tv.setTextSize(TypedValue.applyDimension(
						TypedValue.COMPLEX_UNIT_SP,
						8,
						r.getDisplayMetrics()));

				convertView = tv;
			}

			tv.setText(getItem(position).toString());
			return tv;
		}
	}

	/**
	 * Retrofit: https://github.com/square/retrofit
	 */
	public static class ApiManager {
	    private interface ApiManagerService {
	        @GET("/weather")
	        Observable<WeatherData> getWeather(@Query("q") String place, @Query("units") String units);
	    }

	    private static final RestAdapter restAdapter = new RestAdapter.Builder()
	        .setEndpoint("http://api.openweathermap.org/data/2.5")
	        .build();

	    private static final ApiManagerService apiManager = restAdapter.create(ApiManagerService.class);

	    public static Observable<WeatherData> getWeather(final String city) {
        	return apiManager.getWeather(city, "metric");
	    }

//	    public static Observable<WeatherData> getWeatherData(final String city) {
//	        return Observable.create(new OnSubscribe<WeatherData>() {
//				@Override
//				public void call(Subscriber<? super WeatherData> subscriber) {
//	            	ThreadUtils.assertNotOnMain();
//	                try {
//	                	LogUtil.v(TAG, "I'm sleepy... ");
//	                	Thread.sleep(5000);
//	                	LogUtil.v(TAG, "Making remote call...");
//	                	subscriber.onNext(apiManager.getWeather(city, "metric"));
//	                	LogUtil.v(TAG, "Request completed");
//	                	subscriber.onCompleted();
//	                }
//	                catch (Exception e) {
//	                	subscriber.onError(e);
//	                }
//				}
//	        })
//	        .subscribeOn(Schedulers.io());
//	    }
	}
}
