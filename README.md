# Matrix Calculator & WiFi RSS Logger App

This project consists of two Android applications built using **Kotlin**, **XML**, and **C++ (Native Code via JNI)**:

1. **Matrix Calculator App**  
2. **WiFi RSS Logger App**  

Each application fulfills specific requirements as per the assignment instructions.

---

## Q1: Matrix Calculator App

### Overview
The **Matrix Calculator** allows users to perform operations on matrices:
- **Addition**
- **Subtraction**
- **Multiplication**
- **Division**

It supports matrices of **any dimension** and uses **C++** via **JNI** (Java Native Interface) for core vector operations.

### Implementation Details

- **UI Activity**:  
  The app starts with an activity that prompts the user to input matrix dimensions. A dynamic grid is generated to enter matrix elements for both matrices A and B.

- **Interface to Accept Input**:  
  The app collects matrix dimensions and elements using dynamically generated EditText fields organized in a GridLayout.

- **C++ Native Code**:  
  Matrix operations (addition, subtraction, multiplication, division) are implemented in **C++** for efficient computation using vector operations.

- **Native Code Integration**:  
  - JNI is used to bridge between Kotlin and C++.
  - `CMakeLists.txt` is configured to build the native C++ library.
  - Native functions are called from Kotlin to perform operations after input collection.

- **Composition and XML Usage**:  
  - Composition principles are followed where modular fragments and reusable components are defined.
  - XML layouts define structure, and IDs are used in Kotlin to reference and modify views dynamically.

### How It Works

1. User provides the matrix dimension.
2. Inputs elements for Matrix A and Matrix B.
3. Selects an operation (Add, Subtract, Multiply, Divide).
4. Result is computed using native C++ code and displayed.

---

## Q2: WiFi RSS Logger App

### Overview
The **WiFi RSS Logger App** scans and logs **Received Signal Strength (RSS)** values of available WiFi Access Points across **three different locations**. It stores RSS data as a **matrix of 100 elements per location** and compares the WiFi signal ranges.

### Implementation Details

- **App Interface**:  
  The main screen lets the user select a location and start logging RSS data.

- **Logging RSS Data**:  
  The app:
  - Continuously scans available WiFi networks.
  - Records RSS values for each AP.
  - Stores up to 100 RSS samples per selected location.

- **Location Handling**:  
  Three preset locations are available for the user to log and differentiate WiFi signal strengths:
  - Location 1
  - Location 2
  - Location 3

- **Comparison and Visualization**:  
  After data logging, the app provides a **comparative view** showing how signal strengths vary across the locations, helping to determine which location has the strongest WiFi signals.

- **Use of XML and Kotlin**:  
  UI elements (buttons, text views) are structured using XML. IDs are accessed in Kotlin for dynamic updates based on scanning results.

- **Scrollable View**:  
  Logged data is displayed in a scrollable TextView for easy viewing of large matrices.

### How It Works

1. User selects one of the three locations.
2. App scans and logs up to 100 RSS samples.
3. Repeat for other locations.
4. After logging, the user can view a comparison of RSS ranges across all three locations.

---

