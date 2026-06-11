# 🍔 FoodFlow

Hey there! Welcome to **FoodFlow**, my capstone project for Android development. I built this app to simulate a real-world food delivery ecosystem. It's not just a "Hello World" app—it handles real-time orders, multiple user roles (Customer, Restaurant, Driver, Admin), and payment logic.

It was a massive learning curve, but I'm super proud of how it turned out. I focused heavily on architecture and clean UI using Jetpack Compose.

## ✨ Features (The "Cool Stuff")

I structured the app so that every user type gets their own dashboard. Here's the breakdown:

### 👤 Customer
*   **Discovery:** Browse restaurants, filter menu items by category, and see newly added items on the home feed.
*   **The Cart:** Add items to the cart, modify quantities, and see a breakdown of fees (delivery, service fee) before checking out.
*   **Order Tracking:** Watch your order status update in real-time (Placed → Preparing → On the Way).
*   **Favorites:** Save your favorite meals locally for quick access later.

### 🍳 Restaurant
*   **Menu Management:** A full dashboard to add, edit, or delete menu items. I added a toggle to set items as "Active" or "Sold Out" instantly.
*   **Order Management:** Accept or reject incoming orders. If a customer pays via Bank Transfer, the restaurant sees a "Verify Payment" button to approve the receipt.
*   **Analytics:** A simple dashboard to see today's revenue and order count.

### 🚗 Driver
*   **Delivery Feed:** See a list of orders that are "Ready for Pickup."
*   **Earnings:** Track earnings by "Today," "This Week," or "This Month."
*   **Order Actions:** Claim an order for delivery and mark it as "Delivered" once the job is done.

### 🛡️ Admin
*   **"God Mode":** Approve or reject applications from users wanting to become Drivers or Restaurants.
*   **Platform Settings:** Configure the global delivery fees, platform commission rates, and the bank account details for manual payments.
*   **Catalog Control:** Manage the global food categories (e.g., Pizza, Burgers).

## 🏗️ Tech Stack & Architecture

I tried to stick to modern Android best practices (Clean Architecture-ish).

### The Stack
*   **Language:** Kotlin
*   **UI:** Jetpack Compose (Material 3)
*   **Backend:** Firebase (Firestore for the database, Firebase Auth for login/registration).
*   **Navigation:** Jetpack Navigation (Compose).
*   **Async:** Kotlin Coroutines & Flows (for real-time updates).
*   **Image Loading:** Coil.
*   **Local Storage:** DataStore (for saving user preferences like Favorites & Themes).
*   **Networking:** OkHttp (used specifically for uploading images to ImgBB).

### The Brains (Architecture)
I used the **MVVM (Model-View-ViewModel)** pattern.
*   **UI (Composables):** Observe state from the ViewModel. They don't know about the database.
*   **ViewModel:** Handles business logic (e.g., calculating cart totals) and survives screen rotations.
*   **Repository:** The single source of truth. It talks to Firebase Firestore and returns data to the ViewModel.

## 🚧 Challenges & Trade-offs

I’m a junior dev, so I had to make some hard choices to keep the scope manageable.

*   **No GPS/Maps:** I skipped the location services logic. Instead, the customer just types their address, and the driver sees it as text. Implementing Google Maps SDK and real-time driver tracking was a bit too complex for the timeline.
*   **Manual Payments:** I didn't integrate Stripe or PayPal. Instead, I built a manual "Bank Transfer" flow. The customer uploads a receipt, and the Restaurant verifies it manually. It mimics the real-world verification process found in my region.
*   **Image Hosting:** I used **ImgBB** for hosting menu images via their API. It's free and easy for a capstone project, but obviously, in a production app, I'd use Firebase Storage directly.

## 🛠️ How to Run the Project

Want to take it for a spin? Here is how to get it running on your machine.

1.  **Clone the repo:**
    ```bash
    git clone https://github.com/yourusername/FoodFlow.git
    ```
2.  **Open in Android Studio:** Open the project folder.
3.  **Firebase Setup (CRITICAL):**
    *   Go to the [Firebase Console](https://console.firebase.google.com/).
    *   Create a new project.
    *   Enable **Authentication** (Email/Password and Google Sign-In).
    *   Enable **Cloud Firestore**.
    *   Download the `google-services.json` file from your Project Settings.
    *   **Place it in the `/app/` folder** (replace the dummy one if present).
4.  **ImgBB (Optional):** The code currently includes an API key for image uploads. If you want to use your own, replace the key in `ImageUploader.kt`.
5.  **Run:** Click the "Run" button in Android Studio on an emulator or physical device.

## Detail
See attached document: [FoodFlow-AUB-B9Y2S2-Andriod-Project-Report.pdf](https://github.com/user-attachments/files/28838415/FoodFlow-AUB-B9Y2S2-Andriod-Project-Report.pdf)
