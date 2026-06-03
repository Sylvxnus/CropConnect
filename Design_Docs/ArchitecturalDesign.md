# Architectural Design / Constraints

## System Overview

The proposed system is a mobile application designed to support the redistribution of surplus allotment produce and improve access to local food support services. The platform aims to connect growers with community organisations and food banks, helping to reduce food waste while improving food accessibility within the local community.

The primary users of the system are producers, food banks, and guests.

- **Producers** can register, log in, and submit surplus produce donations to participating food banks.
- **Food Banks** can manage incoming donations, monitor stock levels, and coordinate the distribution of produce.
- **Guests** can access the interactive food bank map and locate nearby services without requiring an account.

The system boundary includes the Android mobile application, a Spring Boot, authentication services, produce listing management, request and collection management, mapping functionality, multilingual support, and assisted access features. External services such as OpenStreetMap is considered outside the system boundary and is used to provide location-based functionality. The system also uses a PostgreSQL database for the secure storage and retrieval of application data.

---

## High-Level Architectural Structure

The system follows a client-server architecture as shown in Figure 1. This structure separates the user interface, application logic and data storage, making the system easier to manage and maintain.

![Figure 1: High-Level Architecture Diagram](https://github.com/user-attachments/assets/4a493b47-f75e-4010-b1fd-bc973e29bd3e)

*Figure 1: High-Level Architecture Diagram*

---

## Architectural Components

| Component | Purpose |
|-----------|---------|
| Authentication | Manages user registration, login, logout, and password recovery. |
| User Management | Stores and manages user profiles, roles, and account information. |
| Produce Listing | Allows producers to create, edit and manage surplus produce listings. |
| Request and Collection | Enables food banks to receive and manage produce donations and coordinate collections. |
| Maps | Integrates OpenStreetMap to display food bank locations and navigation information. |
| Translation | Provides multilingual support for users requiring English or Urdu interfaces. |
| Assisted Access | Supports users with low digital literacy through simplified navigation and larger interface elements. |
| Spring Boot REST API | Handles business logic, processes requests from the mobile application, and manages communication with the database. |
| PostgreSQL Database | Stores user information, produce listings, donations, requests, collections and other application data. |

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
- User preferences

---

## Design Rationale

The chosen architecture was selected to provide a balance between functionality, maintainability and scalability. A client-server architecture was adopted as it separates the user interface, application logic and data storage into distinct layers, making the system easier to manage and extend in the future.

### Android Studio and Java

Android Studio and Java were selected as the primary development tools due to their suitability for Android application development and the team's existing experience with these technologies.

### Spring Boot

Spring Boot was selected as the backend framework due to its compatibility with Java, built-in support for RESTful APIs, and seamless integration with PostgreSQL. It provides a structured and scalable approach to handling application logic and data management.

### PostgreSQL

PostgreSQL was chosen as the database solution because of its reliability, security features and compatibility with the university's database servers.

### OpenStreetMap

OpenStreetMap was selected to support location-based functionality, allowing users to locate nearby food banks and collection points without relying on commercial mapping services.

### Summary

Overall, the chosen architecture provides a structured and practical solution that meets the requirements of the project while remaining achievable within the available development timeframe.
