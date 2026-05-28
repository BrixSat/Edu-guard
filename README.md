# EduGuard

EduGuard is an Android-based student monitoring and device control application designed to help manage and restrict device usage in educational environments. The app uses Android Device Policy Manager (MDM) APIs to enforce policies and ensure focused learning.

## Features
- Student device usage monitoring
- App restrictions and policy enforcement
- Kiosk mode (Lock Task Mode) support
- Background monitoring services
- Admin-controlled policy management
- Secure authentication
- REST API integration for backend communication

## Technologies Used
- Android (Java, XML)
- Jetpack Components
- Device Policy Manager (MDM)
- Background Services
- Node.js & Express (Backend)
- RESTful APIs

## Project Structure
- Admin module for policy management
- Student agent for enforcing rules
- Background services for monitoring activity
- Backend server for authentication and data handling

## Setup Instructions
1. Clone the repository:
   git clone https://github.com/Rohitrrr384/Edu-guard.git

2. Open the project in Android Studio.

3. Sync Gradle files.

4. Run the app on a physical device (MDM features require a real device).

## Usage
- Admin configures device policies.
- Student device receives and applies policies automatically.
- Activity data is monitored and logged securely.

## Use Case
- Educational institutions
- Parental control systems
- Secure learning environments

## Future Enhancements
- Cloud-based admin dashboard
- Real-time policy updates
- Detailed analytics and reports
- Multi-device management support

## Author
Rohit Rathod
