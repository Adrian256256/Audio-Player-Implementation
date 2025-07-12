# GlobalWaves Project

**Author:** Harea Teodor-Adrian  
**Class:** 323CA

---

## Overview

This project simulates an application that plays music and podcasts. It supports playing single songs, songs from playlists, albums, and podcasts. The goal is to manage various pieces of information in a database to provide a seamless user experience.

The project is built on a foundation created in a previous stage and enhanced to allow future extensibility of features.

---

## Architecture & Packages

- **database**: Contains auxiliary classes that underpin the implementation.  
  Example: `ArtistPage` stores artist page info, `Event` stores event details.

- **programInterface**: Contains user-facing classes with methods for commands, and fields that manage relations between classes through aggregation, composition, or inheritance.

---

## Key Design Concepts

- **Singleton Pattern**  
  Used for classes where multiple instantiations are unnecessary, such as `ExecuteCommand`, `SearchBar`, and `GeneralStatistics`.

- **Class Responsibilities**  
  Each class contains methods directly related to its role, making the code intuitive and easy to maintain.  
  For example, `GeneralStatistics` has methods for generating statistics, while `UserManagement` manages user-related commands and data.

- **Command Execution Flow**  
  - Commands are read from a JSON file into an array of `Command` objects inside the `ExecuteCommand` singleton.  
  - Each command is handled via conditional statements invoking specific methods.  
  - The class hierarchy (ExecuteCommand → NavigateClass → Player → StatusClass → PlayListClass) allows command execution to be distributed across classes for modularity and clarity.  
  - Use of `super` allows access to methods of parent classes without additional parameters or instantiations.

- **Data Management**  
  - `UserManagement` holds the user database (an array of user classes).  
  - `PageManagementHub` manages pages like `ArtistPage` and `HostPage`.  
  - `PrincipalDatabase` aggregates extensive app data such as user loads, likes, searches, selections, statuses, playlists, users, and pages. This supports modular data storage and efficient data handling.

---

## PrincipalDatabase Fields

- `loaded`: List of user load records  
- `likesByUsernames`: Stores users and the songs they liked  
- `likesBySongs`: Stores songs and the users who liked them  
- `searchData`: Stores user search records and results  
- `selected`: Stores user selections with entity types and properties  
- `statuses`: Stores user listening states (current song, time, repeat/shuffle mode, album, etc.)  
- `playlistPrincipal`: Stores playlists and related info  
- `users` and `pages`: Manage users and page information  

---

## Extensibility

The codebase is structured to allow easy extension of features by adding new commands and methods across the class hierarchy, leveraging inheritance and composition.

---
