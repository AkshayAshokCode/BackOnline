# Publishing BackOnline 1.0.0 on Google Play

Your steps, in order. Everything Claude prepared is in this `play/` folder.

| File | Use |
|---|---|
| `app-release.aab` (`app/build/outputs/bundle/release/`) | The upload, signed with the upload key |
| `listing-en.md` | Title, descriptions, release notes |
| `graphics/` | Icon, feature graphic, phone and TV screenshots, TV banner |
| `privacy-policy.html` | Live at https://akshayashokcode.github.io/BackOnline/play/privacy-policy.html |
| `fgs-demo.mp4` | Video for the foreground-service declaration (step 4) |

## 1. Back up the upload key (do this first)

- Key file: `~/Keystores/backonline-upload.jks`
- Password: in `~/AndroidStudioProjects/BackOnline/keystore.properties` (the same password is used for the store and the key; alias `upload`)
- Save **both** in your password manager or another safe place. Without them you cannot publish updates. If you ever lose them, Google can reset the upload key, but that takes days.
- Neither file is in git, and both should stay out of it.

## 2. Put the privacy policy online

Play needs a public URL. The BackOnline repo is private, so GitHub Pages there needs a paid plan. Pick one:
- **A small public repo** (for example `AkshayAshokCode/backonline-site`) with `privacy-policy.html` as `index.html`, then turn on GitHub Pages. The URL is `https://akshayashokcode.github.io/backonline-site/`. Claude can set this up if you say so.
- Or Google Sites, or any site you already own.

## 3. Create the app

Play Console → **Create app**:
- App name: `BackOnline`
- Default language: English (United States)
- App or game: **App**. Free or paid: **Free**
- Tick the declarations → **Create app**

## 4. App content (Policy → App content)

| Form | Answer |
|---|---|
| **Privacy policy** | https://akshayashokcode.github.io/BackOnline/play/privacy-policy.html |
| **Ads** | No, my app does not contain ads |
| **App access** | All functionality is available without special access |
| **Content rating** | Category: *All other app types*. Answer **No** to every question (no violence, no user content, no sharing of location, no purchases, no web browsing). Expected rating: Everyone / PEGI 3 |
| **Target audience** | 13–15, 16–17 and 18+. Do **not** include under 13 (that adds the Families policy). Not appealing to children |
| **News app** | No |
| **Data safety** | Does your app collect or share any required user data types? **No**. That is the whole form: the app has no server, analytics or ads. Data stays on the device, and backup is off |
| **Government app** | No |
| **Financial features** | My app doesn't provide any financial features |
| **Health** | My app does not have any health features |
| **Foreground service permissions** | See below |

### Foreground service declaration

Select **Special use** (`FOREGROUND_SERVICE_SPECIAL_USE`). Description:

```
BackOnline alerts the user when their internet connection works again. The user taps "Notify me when it is back" during an outage. A foreground service then watches the connection (Android ConnectivityManager callbacks) until the internet is validated again, rings or notifies once, and stops. It needs to keep running while the app is closed, because the user puts the phone down and waits. It shows an ongoing notification with a "Stop waiting" action and stops by itself after 12 hours. No existing foreground service type covers waiting for network connectivity to return.

On Android TV only, when the user leaves "Automatic alerts" on, a second foreground service watches the connection while the TV is on and plays a sound when the internet comes back after an outage. TVs are always plugged in, and the user can turn this off on the app's main screen.
```

- Impact if interrupted: *the user misses the alert and has to keep checking the connection by hand*.
- Video: upload `fgs-demo.mp4` to YouTube as **Unlisted** and paste the link.

## 5. Store listing (Grow users → Store presence → Main store listing)

Paste from `listing-en.md` and upload the files from `graphics/`:
- App icon: `icon-512.png`
- Feature graphic: `feature-graphic.png`
- Phone screenshots: `phone-1.png` … `phone-6.png`, in order
- Skip the tablet slots for now (optional)

Store settings: category **Tools**, contact email `akshayashokan1054@gmail.com`.

## 6. Android TV (Test and release → Advanced settings → Form factors)

- **Add form factor → Android TV**, then opt in.
- In the store listing, a new **Android TV** section appears. Upload `tv-banner-1280.png` and `tv-1.png` … `tv-3.png`.
- Google reviews TV apps separately. If TV review fails, the phone app is still published; Play Console then shows what to fix.

## 7. Test on your own phone first (recommended)

Test and release → Testing → **Internal testing**:
- Create a release and upload `app-release.aab`. Accept **Play App Signing** when asked: Google keeps the app signing key, and you keep the upload key.
- Release notes: from `listing-en.md`.
- Add yourself as a tester, open the opt-in link on your phone and install from Play.
- Test one real outage: arm the alert, switch the router off for a few minutes, lock the phone and put it away. Then switch the router back on.

## 8. Publish

- Test and release → **Production** → Countries / regions: **Add all**.
- **Promote the internal release** (or create a new release with the same AAB), then **Send for review**.
- First reviews usually take a few days. Foreground-service declarations can add time.

## After launch

- Watch **Statistics → Search terms** and **Android vitals** in the first weeks.
- Next in the brief: translations and localised listings (Spanish, Portuguese, Hindi first).
