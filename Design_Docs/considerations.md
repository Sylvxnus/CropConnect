# ⚠️ Risk Considerations

This document outlines the key risk areas identified for the allotment-to-food-bank donation app.

---

## 📋 Table of Contents

- [Data Privacy and GDPR](#data-privacy-and-gdpr)
- [Food Safety](#food-safety)
- [Technical Risks](#technical-risks)
- [Trust and Misuse](#trust-and-misuse)
- [Legal and Liability](#legal-and-liability)
- [Social and Ethical](#social-and-ethical)

---

## 🔒 Data Privacy and GDPR

### Personal Data Collection
As we are collecting a large amount of data on the food banks such as its location, password and email we need to ensure that users' data is safely stored, including a privacy policy in the app and a way of allowing users to request deletion of their data.

### Location Data Sensitivity
Storing users' allotment locations alongside their user identities could expose allotment owners to risk. We should therefore consider how we could anonymise or aggregate location data where possible.

---

## 🥦 Food Safety

### Food Hygiene Regulations
Anyone giving away food for human consumption can be subject to the Food Safety Act 1990 requirements. Donors could be liable if donated food were to cause illness. We should consider disclaimers and guidance on what foods are safe to donate.

### Allergen Information
We should consider prompting donors to note relevant information such as what plants were in the vicinity of the donated food, what products have been used on the plants, and if there has been any potential cross-contamination.

### Shelf Life and Storage
Due to fresh produce spoiling quickly, we must inform donors to only donate fresh foods which have shown little to no signs of aging. We could support this by including a best-before guide on commonly donated items.

---

## 💻 Technical Risks

### App Availability
We need to consider the potential of the app going down temporarily, as this could cause food to go uncollected and wasted. We should implement a backup notification system to prevent any additional waste.

### Data Loss
We are going to need reliable backup options for our data to prevent donors' credits being unaccounted for. We should implement a regular backup and recovery plan to prevent this.

---

## 🚨 Trust and Misuse

### Fraudulent Donations
Users could claim to donate food when they never did, earning credits dishonestly. We should consider a confirmation step where the food bank has to acknowledge the donation before credits are awarded.

### Gaming the Credit System
If credits reduce rent significantly it creates an incentive to abuse the system. We need to define clear rules for how credits are calculated and how those credits can be used per allotment term.

### Unverified Users
Anyone could sign up claiming to be an allotment holder or a food bank. We therefore need to include an approval process to avoid bad actors manipulating the platform.

---

## ⚖️ Legal and Liability

### Liability for Food Harm
If someone were to become ill from donated food, we need to outline who is legally responsible. This must be clearly covered in our terms of service.

### Consumer Credit Regulations
The credits-for-rent-reduction system could be interpreted as a financial service depending on how it is structured. We want to identify any legal issues which could arise before launch.

### Accessibility Obligations
We must ensure the app is usable by those with disabilities, as it could be identified as a public-facing service. To address this, we should include accessibility features such as colour blindness settings and larger font options.

---

## 🌍 Social and Ethical

### Digital Exclusion
We need to ensure that some users aren't excluded from using the app due to being inexperienced with smartphones. We should aim to make it as simple as possible, especially given that the average age of allotment owners tends to be somewhat older.

### Dependency Risk
We need to implement redundancies for food banks which could become reliant on our application, given the unconfirmed lifespan of the app. We need to consider a shutdown plan to prevent vulnerable people from losing access to their food source.
