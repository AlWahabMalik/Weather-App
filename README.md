# Weather App ☀️🌧️⛅

This Weather App is an Android application built using **Java** and **XML** to provide real-time weather information for any city in the world. It fetches data from the **OpenWeather API** and provides detailed weather updates including temperature, humidity, wind speed, sunrise/sunset timings, and more. Additionally, the app dynamically updates animations using **Lottie** to reflect the current weather conditions such as clear, rain, snow, or cloudy skies.

## Features 🌟

- **Real-time Weather Information**: Get current weather data by entering any city name.
- **Search Functionality**: A built-in search view allows users to type and fetch weather details for different cities.
- **Dynamic Lottie Animations**: Weather animations change dynamically to match the current weather condition (e.g., sunny, rainy, cloudy, snowy).
- **Temperature & Humidity**: Displays the current temperature in Celsius, as well as the minimum, maximum, and humidity levels.
- **Wind Speed**: Provides real-time wind speed updates in meters per second.
- **Sunrise & Sunset**: Shows the exact sunrise and sunset times, converting Unix timestamps into a human-readable format.
- **Day & Date**: Automatically updates and displays the current day and date.
- **Persistent Last State**: Saves the last city and its weather conditions, so users see the last fetched data upon reopening the app.
- **Error Handling**: Shows a toast message if the user enters an invalid or non-existent city name.

## Tech Stack 🛠️

- **Java**: Core language used for developing the logic and backend of the app.
- **XML**: For designing the layout and UI elements.
- **OkHttp**: Used for making API calls to OpenWeather API to fetch weather data.
- **Lottie**: For rendering beautiful weather animations such as sunny, rainy, cloudy, and snowy.
- **OpenWeather API**: Provides weather data including temperature, humidity, wind speed, etc.

## Usage 📝

1. Open the app.
2. Use the search bar to enter any city name.
3. View the real-time weather details and dynamic weather animations.
4. The app will store the last searched city's weather, displaying it when the app is reopened.
