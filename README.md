# gadgetbridge_smn_integration

Android app that fetches weather from SMN (`https://ws.smn.gob.ar/map_items/weather`) and sends it to Gadgetbridge through the external weather provider broadcast API.

## Features
- Current or fixed location mode
- Configurable update interval (minimum 15 minutes)
- Background updates with WorkManager
- Manual "Update now" action
- GitHub Actions workflow that builds and publishes a debug APK artifact

## Gadgetbridge integration
The app sends a broadcast intent with action:
`nodomain.freeyourgadget.gadgetbridge.externalweather`

## Build
```bash
./gradlew :app:assembleDebug
```
