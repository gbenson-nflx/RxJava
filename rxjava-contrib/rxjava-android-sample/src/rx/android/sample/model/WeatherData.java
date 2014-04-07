package rx.android.sample.model;

import java.util.List;

public class WeatherData {

	public Coordinates coord;
    public Local sys;
    public List<Weather> weather;
    public String base;
    public Main main;
    public Wind wind;
    public Cloud clouds;

    public long id;
    public long dt;
    public String name;
    public int cod;

    public static class Coordinates {
        public double lat;
        public double lon;

		@Override
		public String toString() {
			return "Coordinates [lat=" + lat + ", lon=" + lon + "]";
		}
    }

    public static class Local {
        public String country;
        public long sunrise;
        public long sunset;

		@Override
		public String toString() {
			return "Local [country=" + country + ", sunrise=" + sunrise
					+ ", sunset=" + sunset + "]";
		}
    }

    public static class Weather {
        public int id;
        public String main;
        public String description;
        public String icon;

		@Override
		public String toString() {
			return "Weather [id=" + id + ", main=" + main + ", description="
					+ description + ", icon=" + icon + "]";
		}
    }

    public static class Main {
        public double temp;
        public double pressure;
        public double humidity;
        public double temp_min;
        public double temp_max;
        public double sea_level;
        public double grnd_level;

		@Override
		public String toString() {
			return "Main [temp=" + temp + ", pressure=" + pressure
					+ ", humidity=" + humidity + ", temp_min=" + temp_min
					+ ", temp_max=" + temp_max + ", sea_level=" + sea_level
					+ ", grnd_level=" + grnd_level + "]";
		}
    }

    public static class Wind {
        public double speed;
        public double deg;

		@Override
		public String toString() {
			return "Wind [speed=" + speed + ", deg=" + deg + "]";
		}
    }

    public static class Rain {
        public int threehourforecast;
    }

    public static class Cloud {
        public int all;
    }

	@Override
	public String toString() {
		return "WeatherData [coord=" + coord + ", sys=" + sys + ", weathers="
				+ weather + ", base=" + base + ", main=" + main + ", wind="
				+ wind + ", clouds=" + clouds + ", id=" + id
				+ ", dt=" + dt + ", name=" + name + ", cod=" + cod + "]";
	}
}
