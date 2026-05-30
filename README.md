# Contexto: Autonomous On-Device AI Agent

## OVERVIEW
Contexto is an on-device AI-powered assistant that actively monitors a user's local context (location, physical activity, ambient noise, calendar, and screen context) to autonomously execute tasks on their behalf using local tool-calling.
Instead of just responding to text prompts, it acts as an agent that anticipates user needs locally.

## 🏗️ ARCHITECTURAL ENGINE (Perceive-Reason-Act)
Contexto operates on a reactive, decoupled pipeline designed for high-performance local inference:

1.  **PERCEPTION (Ingress)**: Environmental sensors (GPS, Activity Recognition, Audio) emit data streams aggregated into a `CurrentContextSnapshot`.
2.  **COGNITION (Reasoning)**: The `CognitiveEngine` processes snapshots, retrieves long-term memory via a `VectorMemoryEngine`, and uses a local LLM to decide on `AgentAction`s.
3.  **EXECUTION (Egress)**: The `ToolRouter` dispatches decided actions to specialized `ActionExecutor` implementations (System Settings, Notifications, Media).

### Project Directory Structure
```text
app/src/main/java/com/contexto/www/
├── perception/              # Environmental Sensing (Ingress)
│   ├── model/               # Context Snapshots & Data Classes
│   ├── producer/            # Sensor Stream Providers
│   └── aggregator/          # StateFlow Aggregation & Throttling
├── cognition/               # Intelligence & Reasoning (The Brain)
│   ├── engine/              # Logic Loop & LLM Orchestration
│   ├── memory/              # Vector Storage & Retrieval
│   └── model/               # AgentAction Definitions
├── execution/               # Tool-Calling & Side Effects (Egress)
│   ├── executor/            # Android OS Tool Implementations
│   └── router/              # Action Dispatcher
└── di/                      # Dependency Injection
```

## 🛠️ The Ultimate Android API Checklist
Building this app involves mastering major pillars of modern Android development:

### 1. The Core AI Engine (The 2026 Standard)
*   **ML Kit GenAI Prompt API & AICore**: Prototype using Gemma 4 locally on-device. No cloud-based APIs.
*   **Agentic Tool Calling**: Local model outputs structured JSON commands to trigger internal system functions.

### 2. Context & Background Awareness (The "Always-On" Agent)
*   **Activity Recognition API**: Detect user state (walking, driving, stationary) to adapt AI behavior.
*   **Geofencing API & Advanced Location**: Trigger autonomous actions based on spatial triggers.
*   **Foreground Services & WorkManager**: Background processing for embeddings and real-time tracking.

### 3. Media & Environment Intelligence
*   **Media3**: Adaptive local "ambient soundscapes" responding to environmental audio focus.
*   **CameraX API**: Visual context parsing for the local AI's memory.

### 4. System Interoperability & Automation
*   **Accessibility Services**: (Optional) For screen context awareness and cross-app automation.
*   **App Actions / Shortcuts API**: System-level integration for agent triggers.
*   **Notifications API**: Interactive UI for agent confirmation and feedback.

### 5. Modern Architecture & Data Pipeline
*   **Jetpack Compose**: Fluid, adaptive UI for standard, foldable, and large screens.
*   **Room + Vector Database**: Structured logs in Room; Unstructured embeddings in a Vector DB for Long-Term Memory.
*   **Kotlin Coroutines & Flow**: High-performance multi-threading for concurrent AI inference and sensor aggregation.
