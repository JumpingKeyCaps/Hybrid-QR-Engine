# Hybrid-QR-Engine : AGSL Dynamic Particle Rendering

![Kotlin](https://img.shields.io/badge/Kotlin-2.0+-blue)
![AGSL](https://img.shields.io/badge/Android_API-33+-green)

Hybrid-QR-Engine is a specialized graphics pipeline for Android that renders QR matrices through a dynamic particle system. By shifting from static blocks to a GPU-driven density map, it creates a "Stealth" visual effect where data is hidden within motion.

---

## The Stealth Engine Concept

The engine interprets the QR bit-matrix as a **Probability Density Map** rather than a static grid of pixels.

### Organic Dithering via AGSL
* **High Density (Data)**: The shader generates a concentrated flow of particles in "Black" modules. Opacity is maintained to ensure sensor detection.
* **Low Density (Background)**: Particles are scattered, blurred, or removed in "White" modules, creating an organic "cloud" effect.
* **The Machine Link**: While the human eye perceives a fluid, moving shape, a smartphone camera captures a frozen frame. The high-frequency clusters of particles reconstruct the necessary contrast for ML Kit / ZXing to decode the data.

---

## GPU Implementation: Shader Modes

The engine leverages three specific AGSL implementations to control the visual interpretation of the QR matrix:

### 1. Binary Alpha Threshold (Shader 1)
Direct management of intensity and transparency based on raw matrix sampling.
* **Logic**: Threshold-based sampling (`isBlack - uInvert`) to define data vs. background.
* **Usage**: High-contrast rendering with out-of-bounds safety, optimized for raw performance.

### 2. Smooth Distance Masking (Shader 2)
Uses radius-based constraints and smooth transitions for a refined, circular aesthetic.
* **Logic**: `smoothstep` interpolation driven by `uShapeSoftness` and `uContrastBoost` within a defined `uRadius`.
* **Usage**: Creates "soft" modules where data blocks are rounded and anti-aliased, improving visual integration while maintaining sensor contrast.

### 3. Chromatic Mix & Interpolation (Shader 3)
Advanced visual camouflage that blends the QR structure directly into the application's color space.
* **Logic**: Color-space mixing (`mix`) between `uDataColor` and `uBgColor` based on data presence.
* **Usage**: Dissolves the rigid QR grid into a fluid chromatic layout, where data is visible to sensors primarily through luminance differentials.

---

## Technical Specifications

* **Graphics API**: AGSL (Android 13+ / API 33+)
* **UI Bridge**: Jetpack Compose `RuntimeShader`
* **Matrix Input**: Optimized for Version 1 (21x21) through Version 4 (33x33).
* **Optimization**: Minimal CPU-to-GPU memory copy using compact bit-buffers.

---

## Core Uniforms Reference

| Uniform | Description |
| :--- | :--- |
| **uModuleSize** | Controls the physical scale of the particle grid. |
| **uInvert** | Toggles between normal and inverted data polarity (0.0 / 1.0). |
| **uShapeSoftness** | Adjusts the blur and diffusion of the particle clusters. |
| **uDataColor** | Defines the RGBA identity of the active data modules. |
| **uBgColor** | Defines the RGBA identity of the background/empty modules. |

---

## Integration

The bridge is handled via the `OrganicQrSurface` component. It takes a pre-computed bit-matrix from the CPU and feeds it directly into the AGSL RuntimeShader. This ensures the rendering logic stays 100% on the GPU while the data structure remains strictly valid for scanning.
