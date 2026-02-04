# ToDo App

ToDo List Android application built with Jetpack Compose. This app allows users to create, manage, and organize their tasks efficiently with a clean and intuitive user interface.

## Features

This application implents:

-   **📝 Task Management**: Create, edit, and delete tasks
-   **💾 Local Storage**: Tasks are saved locally on the device using a **SQLite** database via the **Room** persistence library
-   **🔔 Deadline Notifications**: Get reminded about upcoming task deadlines with push notifications, scheduled using WorkManager
-   **🗂️ Task Details**: Each task can include:
    -   Title and Description
    -   Creation and Due Time (with a date and time picker)
    -   Status (Completed / Not Completed)
    -   Category
    -   Notification toggle
-   **🔍 Filtering & Sorting**:
    -   **Search**: Find tasks by title, description, or category
    -   **Sort**: Tasks are automatically sorted by the nearest due time
    -   **Filter**: Option to hide completed tasks from the main list
-   **⚙️ Application Settings**:
    -   Customize the notification lead time
-   **🔗 Deep Linking**: Tapping a notification directly opens the corresponding task details within the app

## 📸 Screenshots

Here is a preview of the application's user interface.

## 📸 Screenshots

Here is a preview of the application's user interface.

<table>
  <tr>
    <td align="center">
      <img src="https://github.com/user-attachments/assets/b747f0d9-d5d6-4390-a4c1-21a933d470fd" alt="Main Task List Screen" width="300"/>
      <br />
      <sub><b>Main Task List</b></sub>
    </td>
    <td align="center">
      <img src="https://github.com/user-attachments/assets/e2c4d025-5af5-495e-866b-2e37062ead69" alt="Add/Edit Task Dialog" width="300"/>
      <br />
      <sub><b>Add Task</b></sub>
    </td>
  </tr>
  <tr>
    <td align="center">
      <img src="https://github.com/user-attachments/assets/b05047c1-7535-431b-9edd-2957990506f8" alt="Settings Screen" width="300"/>
      <br />
      <sub><b>Settings Screen</b></sub>
    </td>
    <td align="center">
      <img src="https://github.com/user-attachments/assets/7d0f8099-8238-4684-ae9b-2d90f4acf52d" alt="Task with Attachments" width="300"/>
      <br />
      <sub><b>Edit Task</b></sub>
    </td>
  </tr>
</table>


    
