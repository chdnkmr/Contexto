## OVERVIEW
Contexto is an on-device AI-powered assistant that actively monitors a user's local context (location, physical activity, ambient noise, calendar, and screen context) to autonomously execute tasks on their behalf using local tool-calling.
Instead of just responding to text prompts, it acts as an agent that anticipates user needs locally.

## 🛠️ The Ultimate Android API Checklist
Building this app will force you to master almost every major pillar of modern Android development.

### 1. The Core AI Engine (The 2026 Standard)
   ML Kit GenAI Prompt API & AICore: Do not use cloud-based OpenAI or Gemini APIs. Prototype using Gemma 4 locally on the device.

Agentic Tool Calling: Train your local model to output structured JSON commands that trigger local Android APIs (e.g., if the user says "I'm heading into a meeting, quiet everything," the model calls your internal system-muting function).

### 2. Context & Background Awareness (The "Always-On" Agent)
   Activity Recognition API: Detect if the user is walking, driving, running, or stationary to change AI behavior.

Geofencing API & Advanced Location: Trigger autonomous actions when entering or leaving specific areas (e.g., "Arrived at the gym -> Open workout playlist + log entry").

Companion Device Manager / Health Connect: Aggregate data from wearables to feed into the AI's daily context summary.

Foreground Services & WorkManager: Use WorkManager for periodic, battery-efficient local AI embeddings generation, and strict Foreground Services for real-time context tracking.

### 3. Media & Environment Intelligence
   Media3 (Exoplayer / Audio Focus): Build an automated local "ambient soundscape" generator that adapts to the environment using ML Kit Audio Classifier (e.g., if ambient noise is high, change audio profiles).

CameraX API: Implement a "visual notes" feature where the camera parses real-time objects or text, turning them into vector embeddings for the local AI to remember.

### 4. System Interoperability & Automation
   Android Accessibility Services: (Optional but powerful) Allow your local agent to read screen context to help automate tedious tasks across other apps.

App Actions / Shortcuts API: Expose your AI agent’s capabilities to the system so users can trigger Contexto via system assistant shortcuts.

Notifications API (Custom & Media style): Use highly interactive notifications with direct reply actions for the agent to ask confirmation before executing high-level tasks.

### 5. Modern Architecture & Data Pipeline
   Jetpack Compose (Compose First): Build a fluid, adaptive UI that looks gorgeous on standard phones, foldables, and large screens.

Room Database + Vector Database (SQLite/ObjectBox): Store standard user logs in Room, but implement a local Vector Database to store text/visual embeddings. This gives your local Gemma 4 model a Long-Term Memory of the user's life.

Kotlin Coroutines & Flow: Manage the intense multi-threaded demands of running on-device AI inference alongside real-time sensor tracking without dropping frames.