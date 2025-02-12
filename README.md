# ⚙️ Simulation Application (For Programming 2 [223 CSE] Course)

A simulation-based application built using **Spring Boot** for the backend and **React.js** for the frontend. The system allows users to create and visualize **queues, machines, and product flow** while simulating real-time processes.

## 📌 Features
- Add and connect **Queues and Machines** dynamically.
- Rearrange elements by **dragging** on the board.
- Simulate **parallel processing** using threads.
- Monitor the **flow of products** through the system.
- Replay previous simulations with saved states.
- Control panel for **zooming, fitting view, and interactivity toggling**.

## 🛠️ Design Patterns Used
### 1️⃣ Singleton Design Pattern
Ensures a **single instance** of the `Monitor` class, which manages all observers and the network state.

### 2️⃣ Observer Design Pattern
Used to **notify objects** when there are changes in the network. The `Monitor` class maintains a list of observers and updates them when needed.

### 3️⃣ Prototype Design Pattern
Facilitates **cloning of objects** like `Machine`, `Queue`, `Product`, and `Network`, improving efficiency by duplicating existing templates.

### 4️⃣ Snapshot (Memento) Design Pattern
Implemented in `NetworkMemento` and `History` classes to **save and restore** network states at different time points.

### 5️⃣ Concurrency Design Pattern
Utilized in `Machine` and `Input` classes to manage **parallel processing** using threads, allowing multiple machines to operate simultaneously.

## 🚀 How to Run the Project

### Backend (Spring Boot)
1. Open the `Backend` folder in **IntelliJ IDEA** or any Java IDE.
2. Run `Application.java`.

### Frontend (React.js)
1. Open the `Frontend` folder in **Visual Studio Code** or any code editor.
2. Open the terminal and run:
   ```sh
   npm install
   npm run dev
3. The frontend will be accessible at http://localhost:5173/.

## 📷 UI Snapshots
![image](https://github.com/user-attachments/assets/edd7ddb8-bf97-42b7-b267-c293b5105883)

## 📘 Contributors
- Badr Elsayed - 22010664
- Adham Anas - 22010601
- Nour Khaled Mohamed - 22011319
- Ali El-Deen Maher - 22010934
