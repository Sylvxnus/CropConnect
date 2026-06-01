# Functionality

## Implementation of Design


## Usability


## Modularity


## Well-Defined Interfaces

### User Interfaces

The system provides dedicated interfaces for recipients, growers, volunteers, and administrators, ensuring that each user can access the functionality relevant to their role.

- **Recipients** can browse available produce, submit requests, and locate nearby food banks.
- **Growers** can create and manage surplus produce listings.
- **Volunteers** can view and coordinate collection activities.
- **Administrators** are provided with additional management functionality to oversee platform activity and maintain system integrity.

All interfaces are designed to provide a consistent user experience, allowing users to navigate between features efficiently. The application also includes multilingual support and assisted access features to improve accessibility for users with varying levels of digital literacy.

### System Interfaces

The system utilises several interfaces to support communication between different components. The Android mobile application acts as the primary interface through which users interact with the platform. User requests, such as account management, produce listings, and collection requests, are processed through the application services layer before being stored within the PostgreSQL database.

The application also integrates with external services where required. The Google Maps API is used to provide location-based functionality, allowing users to view nearby food banks and collection points directly within the application.

These interfaces ensure that data can be exchanged efficiently between system components whilst maintaining a clear separation between the user interface, application logic, and data storage layers.

### Interface Design Considerations

The interfaces were designed with accessibility and usability in mind to ensure that the application can be used by a wide range of users. Multilingual support has been incorporated to provide both English and Urdu interfaces, improving accessibility for users within the local community.

In addition, an **Assisted Access** mode is included to support users with lower levels of digital literacy through:

- Simplified navigation
- Larger interface elements
- Clearer user interactions

A consistent interface design has also been maintained throughout the application to improve usability and reduce the learning curve for new users. These considerations help ensure that users can access key functionality efficiently regardless of their technical experience.
