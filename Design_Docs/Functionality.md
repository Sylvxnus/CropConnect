# Functionality

## Implementation of Design


## Usability


## Modularity


## Well-Defined Interfaces

### User Interfaces

The system provides dedicated interfaces for guest users, producers, and food banks, ensuring that each user can access functionality relevant to their role.

- **Guest users** can search for nearby food banks, view food bank information, and use the map to locate food support services.

- **Producers** can register, log in, view their dashboard, submit donations, view credits, and access weather advice.

- **Food banks** can log in, view dashboard information, manage stock, and process pending donations.

All interfaces are designed to provide a consistent user experience, allowing users to navigate between features efficiently. The application includes basic multilingual support through English and Urdu options on the main entry screen.

### System Interfaces

The system uses several interfaces to support communication between different components. The Android mobile application acts as the primary interface through which users interact with the platform. User actions, such as registration, login, food bank search, donation submission and stock updates, are processed through backend REST API endpoints before being stored or retrieved from the PostgreSQL database.

The frontend communicates with the backend mainly through Retrofit API calls, with some donation-related functionality using direct HTTP requests. The backend exposes REST endpoints for producers, food banks, products and donations.

The application also integrates with mapping functionality through OSMDroid and OpenStreetMap. This allows users to view food bank locations directly within the application.

These interfaces ensure that data can be exchanged between system components whilst maintaining a separation between the user interface, application logic and data storage layers.

### Interface Design Considerations

The interfaces were designed with usability in mind to ensure that the application can be used by different user groups. The role-based structure separates guest, producer and food bank journeys so that users are only shown the features they need.

Basic multilingual support has been incorporated through English and Urdu options on the main entry screen. A consistent interface design has also been maintained throughout the application to improve usability and reduce the learning curve for new users.

Assisted Access mode was considered during the design stage but was not implemented in the final application. It remains a possible future improvement.
