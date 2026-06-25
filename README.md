# WeatherApp

An Android weather application built as part of the GCash Android Developer assessment.
Connects to the OpenWeatherMap API to display current weather and maintain a local history of fetched records.

---

## Features

- **Registration & Sign In** — local Room-based auth with SHA-256 hashed passwords
- **Current Weather tab** — shows city, country, temperature in Celsius, sunrise/sunset times, and a weather icon
- **Weather History tab** — list of weather records saved each time the app is opened
- **Smart icon rule** — clear sky shows sun icon during the day, moon icon after 6 PM based on when the weather was fetched
- **City search** — search by city name as fallback when location permission is denied
- **Offline-aware** — shows a proper error message with retry option when there's no internet

---

## Architecture

Clean Architecture with MVVM presentation layer, single-module, package-based separation.

```
com.misspathan.weatherapp
├── data/
│   ├── local/          Room database, DAOs, entities
│   ├── remote/         Retrofit API service, response DTOs
│   └── repository/     Repository implementations + mappers
├── domain/
│   ├── model/          Clean domain models (WeatherInfo, User, Result)
│   ├── repository/     Repository interfaces
│   └── usecase/        Business logic use cases
├── presentation/
│   ├── auth/           Login and Register screens + AuthViewModel
│   ├── weather/        Current weather screen + WeatherViewModel
│   ├── history/        History screen
│   └── navigation/     Screen route definitions
├── di/                 Manual AppContainer (no Hilt)
└── util/               WeatherIconMapper, TimeFormatter, SessionManager
```

**Why no Hilt?** Manual DI via `AppContainer` keeps the dependency graph explicit and easy to trace. For a single-module app of this size it's cleaner than pulling in annotation processing.

---

## Tech Stack

| Layer | Library |
|---|---|
| UI | Jetpack Compose + Material3 |
| Navigation | Navigation Compose |
| Networking | Retrofit 2 + OkHttp 4 |
| Local DB | Room |
| Async | Kotlin Coroutines + Flow |
| Location | FusedLocationProviderClient |
| Testing | JUnit4 + MockK + kotlinx-coroutines-test |

---

## API Key Setup

This project uses the [OpenWeatherMap API](https://openweathermap.org/api/current).

**The API key is not included in the repository.** To run the project:

1. Register at [openweathermap.org](https://openweathermap.org) and get a free API key
2. Open the `local.properties` file in the **root of the project** (same level as `settings.gradle.kts`)
3. Add the following line:

```
WEATHER_API_KEY=your_api_key_here
```

4. Sync and build. The key is injected into `BuildConfig` at compile time and never hits source control.

> `local.properties` is in `.gitignore` — it will never be committed.

---

## Security Measures

- API key stored in `local.properties`, injected via `BuildConfig` — never in source
- `local.properties` excluded via `.gitignore`
- HTTPS enforced via `network_security_config.xml` (`cleartextTrafficPermitted="false"`)
- OkHttp logging interceptor active only in `DEBUG` builds
- ProGuard/R8 enabled in release with rules for Retrofit, Room, and Gson
- Passwords hashed with SHA-256 before storing in Room — plaintext never persisted
- Session persisted via `SharedPreferences` — only non-sensitive data (user ID + name) stored

---

## Running the App

```bash
# clone
git clone https://github.com/MissPathan/weather-app-android.git

# add your API key to local.properties at project root
# open local.properties and add: WEATHER_API_KEY=your_api_key_here

# build and install
./gradlew installDebug
```

---

## Running Tests

```bash
./gradlew test
```

Tests cover:
- `GetCurrentWeatherUseCase` — success and error paths
- `RegisterUseCase` — all input validation rules
- `WeatherViewModel` — state updates, error handling, save-to-history trigger

---

## Notes

- History saves one record per app open (ViewModel session flag resets on process death)
- History is capped at 50 records to keep the DB size reasonable
- If location permission is denied, a city search bar is available as fallback
- Moon/sun icon in history is based on the time the record was fetched, not current device time
