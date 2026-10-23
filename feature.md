# BackOnline — product brief

Everything discussed so far about turning **Wi-Fi Pulse** (the tool inside AndroidSensors since 4.2.0) into a **separate app with its own brand**, built for Play Store search.

Status: **planning**. **BackOnline** is a separate app, not part of AndroidSensors.

**Name: BackOnline** (final for the repo and brand). Recommended store title: **BackOnline: Wi-Fi Down Alert** — see [section 5](#5-search-keywords-and-naming-research).
Last updated: 2026-10-24.

---

## 1. The problem

During an internet outage, people keep picking up their phone to check whether the internet is back. It is frustrating and wastes attention.

**Product promise:** *Stop checking. We will tell you.*
Arm the alert once, put the phone down, and the device tells you the moment the internet **really** works again.

**Design test for every decision:** *the user never has to look at the device until the alert fires.*

---

## 2. Positioning and brand

**Why a standalone app (not only a tool inside AndroidSensors)**
- People search for the problem ("internet back alert", "internet down", "notify when internet returns"), not for a sensor toolkit.
- A dedicated listing can target those words in title, short description and screenshots.
- Clearer pitch, focused ratings and reviews.
- Room for features that would crowd a sensor app (widget, outage log, TV, reboot resume).

**Trade-offs accepted**
- Starts at 0 installs (AndroidSensors has 4k+).
- Two apps to maintain: releases, Play Console setup, foreground-service declaration, policy reviews.
- Play's repetitive-content policy: the standalone app must clearly do **more** than the AndroidSensors tool, not just look different.
- Wi-Fi Pulse stays inside AndroidSensors for existing users; a "Get the full app" link can come later.

**Brand direction: completely separate from AndroidSensors**
| | AndroidSensors | Standalone app |
|---|---|---|
| Feeling | Technical, sci-fi HUD | Calm, reassuring, friendly |
| Theme | Dark only, cyan accents | Soft off-white + dark mode |
| Colours | Cyan, purple, red | **Green** = online, **warm amber** = waiting, muted red = disconnected (avoid alarming red as the main colour) |
| Motif | Radar, dot grid | A **pulse / heartbeat line that turns into ✓** when the internet is back |
| Layout | Dense HUD | One huge status, one big button, almost nothing else |

**Name** (details and evidence in [section 5](#5-search-keywords-and-naming-research))
- Drop "Wi-Fi" from the brand: the app also watches mobile data (and Ethernet on TV), and "Wi-Fi" is a Wi-Fi Alliance trademark that, as understood, should not be part of a brand name — verify before committing.
- Keep the brand short so the strongest search phrase fits in the 30-character title.
- **Chosen: "BackOnline"** (one word) — instantly understandable in every language, unused on Play, ownable as a name, and it matches the app's best moment: *"✓ You're back online."*
- Earlier candidates: *NetBack* (risk of confusion with "Netbank" bank apps), *NetBeat* (brandable, but doesn't say "back"), *NetBell*, *Back Online* as two words.

**Icon:** a simple pulse line ending in a check mark; solid colours; readable at 48 px. Avoid the generic Wi-Fi fan symbol used by hundreds of apps.

---

## 3. Global reach

**The app is for everyone, everywhere — not limited to any region.** Internet outages, captive Wi-Fi and lost mobile signal happen in every country.

**Availability**
- Publish in **every country where Google Play operates**; no country restrictions.
- Google Play is not available in mainland China, so the Simplified Chinese listing mainly reaches Chinese speakers elsewhere; Traditional Chinese covers Taiwan and Hong Kong.

**Languages (app UI + store listing)**
| Tier | When | Languages |
|---|---|---|
| **1** | Launch | English, Spanish, Portuguese (Brazil), Hindi, Chinese (Simplified) *(already translated in Wi-Fi Pulse)* + Indonesian, Arabic, French, Russian, German, Japanese, Korean, Turkish, Vietnamese, Bengali |
| **2** | Soon after | Chinese (Traditional), Urdu, Persian, Thai, Italian, Polish, Ukrainian, Filipino, Malay |
| **3** | Later | Other Play Store languages, prioritised by installs and Play Console country data |

- Pick the order using Android's largest language groups first, then confirm with real install data per country after launch.
- **Right-to-left** languages (Arabic, Urdu, Persian) need the layout mirrored and tested, including the status board, the alert notification and the TV screen.
- Have each language checked by a native speaker; the global copy rules (short, literal, no idioms) make translation easier and safer.
- Keywords must be researched **per language** (local phrasing differs: *se cayó el internet*, *internet caiu*, *net nahi chal raha*) — don't translate the English keywords.

**Things that work everywhere by design**
- Dates and times use the phone's locale (12/24-hour, local digits).
- No location, phone number, account or server needed; the app works offline apart from the system's own internet check.
- No provider names, currencies or country-specific references in the app or listing.

**To verify**
- Android's internet check ("validation") contacts Google-run test servers. In networks that block or alter them, the phone may report "connected, no internet" even when the internet works — and Wi-Fi Pulse would follow that. Worth testing on networks with heavy filtering before claiming accuracy everywhere.

---

## 4. Play Store listing (search optimisation)

**Title** (max 30 characters)
| Option | Characters | Strength |
|---|---|---|
| **BackOnline: Wi-Fi Down Alert** (recommended) | 28 | "**wifi down alert**" is a searched phrase; "down alert" also overlaps "internet down alert" |
| BackOnline: Outage Alert | 24 | "outage alert app" / "internet outage alert" |
| BackOnline: Internet Down Alert | 31 ✗ | Ideal phrase, one character over the limit |

"Wi-Fi" in the **title** is descriptive use; the trademark concern applies to the **brand name**, which BackOnline avoids.

**Short description** (max 80; lead with the search phrases the title doesn't carry): e.g. *Internet down? Wi-Fi connected but no internet? Get an alarm when it's back.*

**Full description:** use the keyword families from [section 5](#5-search-keywords-and-naming-research) naturally a few times. No keyword stuffing (Play penalises it). Say "internet down" and "outage" clearly, because "alert" alone also matches anti-theft apps. **Never use internet provider names** (Jio, Spectrum, Claro…) — Play's metadata policy forbids other brands' names.

**How Play search works (why the title matters)**
- **Eligibility:** Play matches the words in the title (strongest), short description (strong) and full description (weaker), per language listing. There is no hidden keyword field — a phrase that isn't in the text mostly won't match.
- **Ranking among eligible apps:** tap-through and install rate from search, ratings and reviews, retention and uninstalls, stability (Android vitals).
- **The name matters indirectly:** an easy-to-understand name like BackOnline gets more taps in a results list, which raises ranking. The keyword part of the title makes the app eligible; the name makes people choose it.
- Icon and first screenshot drive taps as much as the name.
- Play listings also appear in Google web search (e.g. "wifi connected but no internet android").
- Don't: repeat keywords, use provider names, or claim "#1" / "best".

**Avoid:** "booster", "refresh", "stabilizer" — a crowded corner with exaggerated claims; honesty is the advantage.

**Screenshots** (one story, short captions)
1. "Internet down? Tap once."
2. "Put your phone down."
3. "We ring when it's back."
4. "Wi-Fi and mobile data"
5. "Live signal strength"
6. (TV) "Your TV tells everyone when it's back"

**Feature graphic:** the pulse line becoming ✓, with the app name.

**Localised listings:** follow the language tiers in [Global reach](#3-global-reach) — Tier 1 languages get both the app and the store listing at launch.

---

## 5. Search keywords and naming research

Research done 2026-10-23 with Google web search autocomplete (Play's own autocomplete endpoint no longer responds) and Google Play search for name conflicts.

**Key finding: people search for the outage ("internet down"), not the recovery ("internet back").**

| Phrase tested | Google suggests? | Meaning |
|---|---|---|
| "alert when internet is back", "internet back notification", "wifi back on alert" | **No suggestions** | Almost nobody searches the "back" phrasing |
| **"internet down alert"** | ✅ internet down alert · **internet outage alert** · **network down alert** · internet down notification · **internet down alarm** | Strongest family — name the outage |
| "notify me when internet" | ✅ "**alert me when my internet goes down**" | Same pattern |
| **"wifi connected but no internet"** | ✅ many variants, incl. "…in mobile" and "**…android tv**" | Very common problem phrase = the app's "connected · no internet" status; supports a TV version |
| "is my internet down" | ✅ followed by provider names | Per-provider checks; don't use provider names in metadata |
| "internet monitor app", "app to check internet connection / stability" | ✅ | Secondary monitor / checker family |
| es "se cayó el internet" · pt "**internet caiu**" | ✅ (often + provider names) | Natural local phrasing |
| hi "**net nahi chal raha**" / "नेट नहीं चल रहा है" | ✅ | Hindi users search in Hinglish and Devanagari |
| es / pt "aplicación / app alerta internet" | ✅ | "alerta internet" works in both |
| zh 网络恢复提醒 / 断网提醒 | No data | Google autocomplete is weak for Chinese; needs another source |

**Keyword plan**
- **Title:** *internet down alert* (or *outage alert*).
- **Short description, first words:** internet down / outage, **Wi-Fi connected but no internet**, notify, alarm.
- **Full description (natural use):** internet outage alert, network down alert, internet down alarm, no internet, internet monitor, check internet connection, notify when internet is back, Wi-Fi and mobile data, signal strength.
- **Localised descriptions:**
  - es: *¿Se cayó el internet?*, *alerta de internet*
  - pt: *A internet caiu?*, *alerta de internet*
  - hi: *नेट नहीं चल रहा?* (plus Hinglish phrases)
  - zh: 断网提醒, 网络恢复提醒 (validate first)

**Name candidates, checked on Google Play**
| Name | On Play? | Notes |
|---|---|---|
| **BackOnline** | ✅ Unused | **Chosen.** Clear everywhere, one word, matches "You're back online"; backonline.com is taken |
| NetBack | ✅ Unused | Says the outcome, but one letter from "Netbank" (bank apps); also an oil-industry term |
| NetBeat | ✅ Unused | Brandable, matches the pulse icon; doesn't say "back" |
| Back Online (two words) | ✅ Unused | Clear, but two words are harder to own |
| **NetBell** | ✅ Unused | "A bell for your internet"; matches the ring feature |
| **Online Again** | ✅ Unused | Clear but long (12) |
| NetPulse | ❌ 8+ apps | Avoid — "Pulse" is crowded among network apps |
| PingMe | ❌ Taken | Phone-number apps own it |
| Reconnected | ⚠️ Used by unrelated apps | Weak |

Other directions considered: alarm-style (*NetAlarm*, *Internet Bell*) and calm-tone (*Breather*, *Waitless*) — the calm ones fit the voice but carry no search signal.

**Before deciding**
- Check trademark databases for the chosen name (the Play check only looked at app titles).
- Find search data for the Chinese phrases.
- After launch, use Play Console's **Search terms** report and **store listing experiments** to confirm which keywords bring installs.

---

## 6. Competitor research (Google Play, 2026-10-23)

**Finding: no app clearly owns "notify me when the internet is back."** Searches ("internet back alert", "internet connection alert", "wifi down notification", "notify when internet is back", "internet outage alarm", "internet down alert", and others; US and India stores) mostly return firewalls, analysers, speed tests and "boosters".

| App | Installs · rating | What it does | vs. this app |
|---|---|---|---|
| WiFi Refresh & Signal Alert (B01T Apps) | 100K+ · 4.0 · ads + IAP | Wi-Fi "refresh", scanner; alerts for no service / low signal / no data | Alerts on **loss**, not on **return**; cluttered utility |
| Signal Alert & Monitor (Mapfulness) | 1K+ · no ads | Alerts travellers when mobile signal reaches a threshold; signal map | Closest idea, but mobile signal only, niche |
| WiFi Disconnect Alarm & Alert (Fat Bytes) | 500+ · ads | **Anti-theft**: alarm when someone turns off Wi-Fi | Different purpose |
| internet connection (com.InternetConnectionCheck) | 500K+ · 4.3 · ads | Basic connection checker | No alert; shows demand for the topic |
| Connection Stabilizer Booster | 10M+ · 4.5 | Keeps mobile connections alive | Different job |
| Home WiFi Alert (DoubleDot) | 10M+ · 4.2 | Network scanner (who is on my Wi-Fi) | Different job despite the name |
| WiFi Monitor: network analyzer | 1M+ · 4.5 | Signal / channel analyser | Different job |
| "Downdetector App" | 10K+ · 1.3 | Status of online services | Different job; low rating suggests unofficial |

**Implications**
- Real gap: no app owns the "tell me when it's back" job. (Search demand sits on "internet down alert", not on "back" — see section 5.)
- Plain keyword-led titles rank at the top → brand + keyword title.
- Competitors are ad-heavy, older-looking utilities → differentiate on: **ad-free, validated-internet alerts (no false alerts), ring, Wi-Fi + mobile data, 5 languages, calm modern design**.

**Limits of this research:** single snapshot; Play personalises search; install figures are ranges; US for all searches, India for four; "WiFi Alert: Your Signal Hound" seen but not opened; Android TV store not yet researched.

Sources: [WiFi Refresh & Signal Alert](https://play.google.com/store/apps/details?id=com.b01t.wifialerts) · [Signal Alert & Monitor](https://play.google.com/store/apps/details?id=com.technomadapps.signalalert) · [WiFi Disconnect Alarm & Alert](https://play.google.com/store/apps/details?id=com.wifidisconnect.alarm) · [internet connection](https://play.google.com/store/apps/details?id=com.InternetConnectionCheck) · [Connection Stabilizer Booster](https://play.google.com/store/apps/details?id=com.connectionstabilizerbooster) · [Home WiFi Alert](https://play.google.com/store/apps/details?id=info.lamatricexiste.networksearch) · [WiFi Monitor](https://play.google.com/store/apps/details?id=com.signalmonitoring.wifimonitoring) · [Downdetector App](https://play.google.com/store/apps/details?id=com.downdetector.app)

---

## 7. Features — phone

### 7.1 Core (already built and shipped in AndroidSensors' Wi-Fi Pulse)

**Arm once, get told when it's back**
- One button: **Notify me when it is back**; also a **Quick Settings tile** to arm or cancel without opening the app.
- Waits until Android **validates** the connection (it really reaches the internet), so it doesn't fire when only the router comes back.
- While connected without internet, asks Android to **re-check every 30 seconds** instead of waiting for its growing back-off (minutes).
- Gives up after **12 hours** with a quiet notification.

**Which connection — automatic, no setting**
| Phone state | Watches |
|---|---|
| Wi-Fi switched **on** (connected or not, e.g. router power cut) | Wi-Fi internet |
| Wi-Fi switched **off** | Mobile data internet |
- Switching Wi-Fi on/off during a wait switches what is watched.
- Not "on **and** connected": in a router power cut the phone falls back to mobile data and the user couldn't arm.

**Alert styles** (chosen next to the button; **Ring is the default**)
- **Ring:** alarm sound, getting louder, plays even on silent, with vibration; stops on Stop / tap / swipe / opening the app, or after 1 minute. A quiet record is left only if nobody responded.
- **Notification:** one high-priority notification.
- **Do Not Disturb respected:** Ring falls back to a notification.
- No full-screen pop-up (Android 14+ restricts it to alarm and calling apps).
- Both say how long you waited: **"Back after 42 minutes"** (counted from arming).

**While waiting**
- Waiting notification on the lock screen with a factual status line and **Stop waiting**.
- Calm, rotating "take a break" lines every 5 minutes; long-outage lines after 1 hour.

**Status and signal**
- Status: **Online on Wi-Fi / mobile data**, **Connected · no internet**, **Disconnected**, plus Wi-Fi off / airplane mode hints with a fix button.
- Signal: `-54 dBm · EXCELLENT · 5 GHz` (Wi-Fi) or `-95 dBm · GOOD · Carrier` (mobile), using the **same levels as the status bar**; the glyph's bars light up by strength.
- No location or Phone permission: never reads the Wi-Fi name, 4G/5G type or cell info.

**Reliability**
- Restarts itself if Android kills its process.
- If a battery saver stopped it, the next app open detects it, resumes the wait (or says it was missed) and links to battery settings.
- Analytics: armed / fired / cancelled / timed out / stopped-by-system (with source, style, target, wait time).

**Permissions:** only **notifications**, asked the first time the user arms the alert (Android 13+).

**Copy rules (users in every country)**
- Short, literal sentences; no idioms, puns, sarcasm, cultural references or contractions; only universal emoji (🙂 🌿 ☕ 📖 🚶).
- Translate the meaning, not the words. Brand name never translated.
- Tone: friendly nudges to rest, talk to someone, get fresh air — the "go do something the internet can't" idea, said literally.

### 7.2 New for the standalone app (to make it clearly more than the AndroidSensors tool)

**Version 1 candidates**
- **Home-screen widget:** live status + one tap to arm.
- **Outage log and uptime:** "Down 3 times this week · 2 h 14 min total"; shareable report for internet provider complaints.
- **Resume after reboot:** keep waiting after the phone restarts (power cuts often restart devices). Needs `RECEIVE_BOOT_COMPLETED` (no prompt); confirm Android 15+ allows a special-use service to start at boot.
- **"Sign-in needed"** status for hotel / café / airport Wi-Fi (captive portal), with **Open sign-in page**; armed alerts wait until the user is signed in and online.
- **Router vs internet provider** diagnosis: a local check whether the router responds → "Your router works. Your internet provider is down." vs "Your phone cannot reach the router."
- **Ethernet support** (needed for TV — see below).

**Later**
- **Connection details card** with **Copy**: link speed, Wi-Fi standard (Wi-Fi 4/5/6/7), band and channel, security (warn on open networks), phone IP / router IP / DNS, private DNS / VPN, metered. Plus a tip like "Your phone supports Wi-Fi 6. Your router uses Wi-Fi 4."
- **Notify when it drops** (reverse alert).
- **Signal Hunter:** walk around with a radar to find the best spot.
- **Room survey:** save signal per room and compare.
- **Latency check** with a "good for video calls / gaming" verdict (user-triggered only).

**Deliberately out of scope**
- **Speed test** (needs a server, data cost, privacy disclosure; crowded category).
- **Network name and nearby-network scanning** (need location permission).
- **4G/5G type and cell details** (need the Phone permission prompt).

---

## 8. Features — Android TV

**Why TV fits**
- The TV is the device that's useless during an outage — people sit there waiting.
- Everyone in the room sees one big screen; nobody has to check a phone.
- Always plugged in → no battery-saver kills (the phone version's biggest reliability risk).
- Stays at home → can watch the home internet when you're away.
- The TV store is less crowded with utilities (not yet researched).

**TV features**
1. **Status board (core):** full-screen, readable from the sofa — "Internet down · waiting since 9:42" → big green **"✓ Back online"** with a chime. D-pad controls only. Left open on screen, because Android TV barely shows notifications and apps can't pop up over other apps.
2. **Screensaver mode:** a calm status screensaver whenever the TV is idle.
3. **Outage log / uptime:** builds history naturally on an always-on device.
4. **Later — TV-to-phone alerts when away:** the TV sees the home internet return and alerts your phone. Needs a small push server — a cost and a privacy change (the app is currently fully offline). A later decision, not v1.

**TV technical notes**
- **Ethernet must be added** — many TVs are wired; the current monitor only knows Wi-Fi and mobile data.
- Landscape only, 10-foot UI, D-pad focus states, 320×180 launcher banner, touchscreen not required; Compose for TV.
- Same Play listing for phone + TV with a TV-specific interface; Play reviews TV releases separately against Android TV quality guidelines.
- The internet-checking logic carries over unchanged; Wi-Fi signal works on Wi-Fi TVs.

---

## 9. Technical notes (from building Wi-Fi Pulse)

- **Foreground service type `specialUse`** while armed; Play Console declaration + demo video required.
- Alert immediately on validation (no stability delay — Doze would stretch it).
- **Doze** pauses app timers in long screen-off idle; the 30-second re-check stops then, so a provider-outage alert can still be late.
- **Battery savers** (some Xiaomi, Samsung, Oppo, Vivo) can kill the service; detect on next open and guide users to battery settings.
- **Edge-to-edge** on Android 15+: call `enableEdgeToEdge()` and handle insets from the start. Play will still list AndroidX's own `EdgeToEdgeApi23/26/29` calls as "deprecated APIs" — harmless.
- **minSdk 24+** (Play automatic protection requires it); targetSdk current.
- **R8 full mode + resource shrinking** from day one; always test a release build on a device.
- **New package name**, e.g. `com.akshayAshokCode.backonline` — permanent, choose with the brand.
- Same developer account as AndroidSensors → cross-promotion both ways.

---

## 10. Validation before building

Use data already collected by AndroidSensors since 4.2.0 (give it 2–4 weeks):
- Firebase: `feature_opened` for `wifi_pulse` vs. other tools; `internet_alert_armed` / `internet_alert_fired` per user; `internet_alert_stopped_by_system` (how often phones kill it).
- Play Console → Search terms: do "internet" / "wifi" searches already bring installs?
- **Go signal:** Wi-Fi Pulse is among the top 2 tools and users arm it repeatedly.

---

## 11. Open decisions

- [x] Name: **BackOnline** (decided 2026-10-24)
- [ ] Trademark search for "BackOnline" (your country's registry + WIPO Global Brand Database)
- [ ] Domain: backonline.com is taken — check backonline.app / getbackonline.com
- [ ] Final store title (recommended *BackOnline: Wi-Fi Down Alert*) and package name (e.g. `com.akshayAshokCode.backonline`)
- [ ] Keyword research per Tier 1 language (Chinese has no data yet)
- [ ] Translations for the new Tier 1 languages, with native review; RTL layout check (Arabic)
- [ ] Version 1 scope: which of widget, outage log, reboot resume, sign-in needed, router diagnosis
- [ ] TV in version 1, or right after?
- [ ] Code: separate project, or share the internet-checking engine with AndroidSensors under a different UI (branding is independent of this)
- [x] Repo: new repo **BackOnline**
- [ ] Research the Android TV store for competitors
- [ ] Wait for 2–4 weeks of 4.2.x data before committing
