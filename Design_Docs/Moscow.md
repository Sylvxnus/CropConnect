# CropConnect — MoSCoW Analysis

> Engineers Without Borders Digital Design Challenge | Launchpad Project Year 2

---

## Must Have

### Functional
- [ ] Users must register and log in with an email address and Birmingham-area postcode
- [ ] Users must select a role on registration: Grower, Recipient, or Volunteer
- [ ] Growers must create surplus listings with type, quantity, and availability dates
- [ ] Growers must specify drop off or volunteer collection
- [ ] Recipients must browse available produce listings
- [ ] Recipients must be able to request a listing
- [ ] Volunteers must view and claim open collection requests
- [ ] Volunteers must earn credits per completed delivery job
- [ ] The app must display a food bank map with directions via an external mapping API
- [ ] The app must support English and Urdu language toggle across all screens
- [ ] Assisted Access Mode must allow one-tap access to the food bank map with no login and no data stored
- [ ] Growers must automatically earn credits per kg of produce donated
- [ ] The app must include a listing flagging and reporting mechanism
- [ ] Distance calculations must use the Haversine algorithm to find nearest listings and food banks

### Non-Functional
- [ ] The app must run on Android 8.0 or above via Android Studio (Java)
- [ ] The backend must be implemented in C++ using the Crow framework
- [ ] The database must be PostgreSQL
- [ ] Core screens must load within 3 seconds on a standard mobile data connection
- [ ] Passwords must be hashed and never stored in plaintext
- [ ] The app must comply with GDPR including the right to account deletion
- [ ] The bilingual toggle must apply instantly without a restart
- [ ] Assisted Access Mode must store no personally identifiable data
- [ ] The app must function on low-end budget Android devices

---

## Should Have

### Functional
- [ ] Growers should view an impact dashboard showing total donated and meals provided
- [ ] Volunteers should view a dashboard showing total deliveries and credits earned
- [ ] Credits should be redeemable via QR code against allotment rent or food vouchers
- [ ] Food bank map should filter by: no referral needed, open now, fresh produce today
- [ ] Map pins should update in real time when a grower logs a donation
- [ ] Recipients should view public transport directions to a collection point
- [ ] Volunteers should mark collection jobs as completed
- [ ] Growers should upload a photo with their listing
- [ ] The app should suggest seasonal crops to growers based on the current month

### Non-Functional
- [ ] The app should be usable with no more than 3 taps to reach any core feature
- [ ] The app should display cached food bank data when offline
- [ ] The codebase should follow consistent naming conventions with inline commenting
- [ ] The app should be modular so individual features can be updated independently

---

## Could Have

### Functional
- [ ] Users could receive in-app notifications when a nearby listing is posted
- [ ] Growers could view a history of all past donations and credits earned
- [ ] The app could suggest the nearest food bank based on the user's postcode
- [ ] The interface could include simplified iconography for users with low literacy
- [ ] The app could integrate a weather API to help growers plan harvests and recipients plan trips

### Non-Functional
- [ ] The app could meet WCAG 2.1 AA accessibility standards
- [ ] The app could support both portrait and landscape orientations
- [ ] The app could implement lazy loading of images to reduce data usage

---

## Won't Have

### Functional
- [ ] Direct allotment plot application submission to Birmingham City Council
- [ ] Integration with Trussell Trust or formal food bank operating systems
- [ ] Punjabi and Bengali language support
- [ ] Push notifications via device OS
- [ ] Formal reputation or rating system for growers and volunteers
- [ ] Cross-referencing of recipient eligibility with benefits or social services data
- [ ] Full Google Play Store deployment
- [ ] Integration with council social services referral pathways
- [ ] Cash payment for volunteers

### Non-Functional
- [ ] iOS support
- [ ] WCAG 2.1 AAA compliance
- [ ] Offline-first full functionality
- [ ] End-to-end encryption of messages between users
- [ ] Multi-device session management
