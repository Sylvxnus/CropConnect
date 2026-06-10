# Architectural Design / Constraints

## System Overview

The proposed system is a mobile application designed to support the redistribution of surplus allotment produce and improve access to local food support services. The platform connects producers with local food banks, helping to reduce food waste while improving food accessibility within the local community.

The primary users of the system are guest users, producers, and food banks.

- **Guest users** can search for nearby food banks, view food bank details, and use the map to locate support services.
- **Producers** can register, log in, view their dashboard, submit surplus produce donations, view credits, and access weather-based growing advice.
- **Food banks** can log in, view their dashboard, manage stock, and process pending donations.

The system boundary includes the Android mobile application, producer and food bank account features, donation management, food bank search, stock management, mapping functionality, and basic multilingual support. External map data is provided through OpenStreetMap using OSMDroid. The system also uses a PostgreSQL database for the storage and retrieval of application data.

---

## High-Level Architectural Structure

The system follows a client-server architecture as shown in Figure 1. This structure separates the user interface, application logic and data storage, making the system easier to manage and maintain.

![Figure 1: High-Level Architecture Diagram](https://github.com/user-attachments/assets/071e8e66-9de0-41aa-9ebb-1bfaa29ac724)

*Figure 1: High-Level Architecture Diagram*

---

## Architectural Components

| Component | Purpose |
|-----------|---------|
| Authentication | Supports producer registration/login and food bank login. |
| User Management | Stores and manages producer and food bank account information. |
| Donation Management | Allows producers to submit surplus produce donations and food banks to process pending donations. |
| Food Bank Search | Allows guest users to search for local food banks and view relevant details. |
| Stock Management | Allows food banks to view and update food stock records. |
| Maps | Uses OSMDroid and OpenStreetMap to display food bank locations. |
| Translation | Provides basic multilingual support, including English and Urdu options on the entry screen. |
| Database | Stores producer information, food bank details, product stock data, and donation records. |

---

## Architectural Constraints and Quality Considerations

### Accessibility

As the application is intended for use by a wide range of users, accessibility was a key consideration throughout the design process. Basic multilingual support was incorporated to improve usability for users within the local community.

### Security

Security was also an important consideration due to the storage of user account information. The system uses password handling for account login, with BCrypt used for producer passwords. Further security improvements would be required in future development, including stronger access control across backend endpoints and a more complete authentication system.

### Device Compatibility

The application is designed primarily for Android devices and should remain compatible with a range of device specifications, including lower-end smartphones. Whilst an internet connection is required for most functionality, the architecture allows for future support of offline features through the local storage of frequently accessed information, such as:

- Food bank locations
- Previously viewed food bank details
- Previously viewed stock or donation information

---

## Design Rationale

The chosen architecture was selected to provide a balance between functionality, maintainability and scalability. A client-server architecture was adopted as it separates the user interface, application logic and data storage into distinct layers, making the system easier to manage and extend in the future.

### Android Studio and Java

Android Studio and Java were selected as the primary development tools due to their suitability for Android application development and the team's existing experience with these technologies.

### Spring Boot

Spring Boot was selected for the backend because it provides a structured way to build REST APIs, connect to a database, and separate the system into controllers, services, repositories and models.

### PostgreSQL

PostgreSQL was chosen as the database solution because of its reliability, security features and compatibility with the university's database servers.

### OSMDroid and OpenStreetMap

OSMDroid and OpenStreetMap were selected to support location-based functionality, allowing users to locate nearby food banks without relying on the Google Maps API.

### Summary

Overall, the chosen architecture provides a structured and practical solution that meets the implemented requirements of the project while remaining achievable within the available development timeframe.
