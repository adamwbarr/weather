## Weather Derivatives

This is a simple framework for analysing weather data, with the goal of being able to price simple weather derivatives.

### Temperature Indices

A common type of weather derivative is one based on temperature, defined in terms of monthly _Heating-Degree-Day (HDD)_ 
index values. The HDD for any particular day is the number of degrees that the temperature on that day was below some 
threshold (generally 65F in the US). So, for example, if the temperature was 50F, the HDD is 15F. The monthly HDD is the
sum of this value over all days in the month.

See [here](https://www.investopedia.com/terms/h/heatingdegreeday.asp) for a more detailed explanation, or 
`co.abarr.weather.temp.Hdd` for the implementation.

### Temperature Models

There are many possible ways of attempting to predict what the HDD will be over a future time period.

Included here is an implementation of the _Alaton Temperature Model_, as defined in [On Modelling and
Pricing Weather Derivatives](https://citeseerx.ist.psu.edu/viewdoc/download?doi=10.1.1.198.6547&rep=rep1&type=pdf)
by Alaton, Djehiche and Stillberger. This can be used to estimate (via Monte Carlo method) the probability
distribution of temperatures for a given time period (and therefore the distribution of the HDD index.)

The basic idea of this model is:

1. Fit (via linear regression) a simple expected temperature model, based on a sin function with a 365-day period,
2. Calculate the residual temperatures by subtracting the expected from observed temperature on each day,
3. Fit an [Ornstein-Uhlenbeck](https://en.wikipedia.org/wiki/Ornstein%E2%80%93Uhlenbeck_process) model to these 
   residuals. This is a mean-reverting model, where the temperature updates randomly each day, but over time reverts to
   the long-term average (ie the expectation from step 1).
   
See `co.abarr.weather.temp.predict.Alaton`.

### Data

Also included is a parser for historic bulk downloads from [OpenWeather](https://openweathermap.org/history-bulk). 
These are CSVs that include a bunch of different weather metrics for a given location: temperature, rainfall, humidity
etc, sampled at half-hourly intervals. Only temperature fields are loaded currently.

See `co.abarr.weather.owm.FromCsv`.

## Build

The project is built using Gradle. To compile the code and run all unit tests:
```
$ gradle test
```