# 🧪 Sequential API Testing Guide

Follow these steps **in order** to verify your backend.

### 🛑 Pre-requisites
1.  **Restart Server**: `.\mvnw spring-boot:run`
2.  **Open Postman**

---

### Step 1: Register (Create Your User)
*   **Method**: `POST`
*   **URL**: `http://localhost:8080/api/auth/register`
*   **Body** (JSON):
    ```json
    {
        "name": "Viva Student",
        "email": "viva@student.com",
        "password": "password123"
    }
    ```
*   **Expected Result**: `200 OK` (Message: "User registered successfully" or token).

---

### Step 2: Login (Get Your Key)
*   **Method**: `POST`
*   **URL**: `http://localhost:8080/api/auth/login`
*   **Body** (JSON):
    ```json
    {
        "email": "viva@student.com",
        "password": "password123"
    }
    ```
*   **Expected Result**: `200 OK` with a `token`.
*   **⚠️ ACTION**: Copy this `token` immediately!

---

### Step 3: Setup Authorization (Crucial!)
For **ALL** following requests:
1.  Go to **Authorization** tab in Postman.
2.  Select Type: **Bearer Token**.
3.  Paste the **Token** from Step 2.

---

### Step 4: Check Dashboard (Should be Empty/Zero)
*   **Method**: `GET`
*   **URL**: `http://localhost:8080/api/user/dashboard`
*   **Expected Result**:
    ```json
    {
        "quizzesAttempted": 0,
        "problemsSolved": 0
    }
    ```

---

### Step 5: Get Skill Categories (Find IDs)
*   **Method**: `GET`
*   **URL**: `http://localhost:8080/api/quiz/categories`
*   **Expected Result**: List of categories.
    *   Note the id of "Java" (likely `4`) or "HTML" (likely `1`).

---

### Step 6: Get Questions (For a Category)
*   **Method**: `GET`
*   **URL**: `http://localhost:8080/api/quiz/questions/1`
*   **Note**: `1` is the ID for HTML.
*   **Expected Result**: List of HTML questions.

---

### Step 7: Create a New Question (Test Admin Feature)
*   **Method**: `POST`
*   **URL**: `http://localhost:8080/api/quiz/questions`
*   **Body** (JSON):
    ```json
    {
        "question": "What is the result of 2+2 in Java?",
        "optionA": "3",
        "optionB": "4",
        "optionC": "22",
        "optionD": "Error",
        "correctAnswer": "B",
        "difficulty": "Easy",
        "category": {
            "id": 4
        }
    }
    ```
    *   *Note: `id: 4` assumes Java is category 4. Check Step 5 results to be sure.*
*   **Expected Result**: `200 OK` (Returns the created question).

---

### Step 8: Verify Question Creation
*   **Method**: `GET`
*   **URL**: `http://localhost:8080/api/quiz/questions/4`
*   **Expected Result**: You should see your new "2+2" question in the list!

---

### Step 9: Simulate "Quiz Completed" (Update Progress)
*   **Method**: `POST`
*   **URL**: `http://localhost:8080/api/user/progress?score=10&problemSolved=false`
*   **Body**: (Empty)
*   **Expected Result**: Updated stats.

---

### Step 10: Check Dashboard Again (Verify Update)
*   **Method**: `GET`
*   **URL**: `http://localhost:8080/api/user/dashboard`
*   **Expected Result**:
    ```json
    {
        "quizzesAttempted": 1,
        "problemsSolved": 0
    }
    ```

---
**🎉 If all steps pass, your backend is stable and viva-ready!**
