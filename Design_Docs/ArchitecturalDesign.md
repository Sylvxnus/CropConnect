# Architectural Design / Constraints

## System Overview

The proposed system is a mobile application designed to support the redistribution of surplus allotment produce and improve access to local food support services. The platform aims to connect growers with community organisations, food banks and volunteers, helping to reduce food waste while improving food accessibility within the local community.

The primary users of the system are recipients, growers, volunteers, and administrators.

- **Recipients** can search for available produce, submit requests, and locate nearby food banks.
- **Growers** can create and manage surplus produce listings.
- **Volunteers** can assist with collection and delivery activities.
- **Administrators** are responsible for overseeing platform activity, managing listings, and maintaining the integrity of the system.

The system boundary includes the Android mobile application, authentication services, produce listing management, request and collection management, mapping functionality, multilingual support, and assisted access features. External services such as Google Maps API are considered outside the system boundary and are used to provide location-based functionality. The system also uses a PostgreSQL database for the secure storage and retrieval of application data.

---

## High-Level Architectural Structure

The system follows a client-server architecture as shown in Figure 1. This structure separates the user interface, application logic and data storage, making the system easier to manage and maintain.

![Figure 1: High-Level Architecture Diagram](https://github.com/user-attachments/assets/071e8e66-9de0-41aa-9ebb-1bfaa29ac724)

*Figure 1: High-Level Architecture Diagram*

---

## Architectural Components

| Component | Purpose |
|-----------|---------|
| Authentication | Manages user registration, login, logout, and password recovery. |
| User Management | Stores and manages user profiles, roles, and account information. |
| Produce Listing | Allows growers to create, edit and manage surplus produce listings. |
| Request and Collection | Enables recipients to request produce and volunteers to coordinate collections. |
| Maps | Integrates Google Maps API to display food bank and collection locations. |
| Translation | Provides multilingual support for users requiring English or Urdu interfaces. |
| Assisted Access | Supports users with low digital literacy through simplified navigation and larger interface elements. |
| Database | Stores user information, produce listings, requests, collections and other application data. |

---

## Architectural Constraints and Quality Considerations

### Accessibility

As the application is intended for use by a wide range of users, accessibility was a key consideration throughout the design process. Features such as multilingual support and simplified interface settings were incorporated to improve usability for users with varying levels of digital literacy.

### Security

Security was also an important consideration due to the storage of user account information. The system must ensure that user data is protected through secure authentication and controlled access to application resources. This is particularly important as the platform supports multiple user roles.

### Device Compatibility

The application is designed primarily for Android devices and should remain compatible with a range of device specifications, including lower-end smartphones. Whilst an internet connection is required for most functionality, the architecture allows for future support of offline features through the local storage of frequently accessed information, such as:

- Previously viewed listings
- Food bank locations

---

## Design Rationale

The chosen architecture was selected to provide a balance between functionality, maintainability and scalability. A client-server architecture was adopted as it separates the user interface, application logic and data storage into distinct layers, making the system easier to manage and extend in the future.

### Android Studio and Java

Android Studio and Java were selected as the primary development tools due to their suitability for Android application development and the team's existing experience with these technologies.

### PostgreSQL

PostgreSQL was chosen as the database solution because of its reliability, security features and compatibility with the university's database servers.

### Google Maps API

The Google Maps API was selected to support location-based functionality, allowing users to locate nearby food banks and collection points.

### Summary

Overall, the chosen architecture provides a structured and practical solution that meets the requirements of the project while remaining achievable within the available development timeframe.
