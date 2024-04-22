### :books: Your Challenge:

Your task is to architect and develop an interactive application composed of a backend and frontend. The backend is to be either Java or Kotlin based, while the frontend should be built using Angular.

### :dart: Core Features:

The core feature on the frontend involves designing a 50x50 grid interface. Each cell in the grid is interactive and can take on a value. When a user clicks on any particular cell, the values in all the cells present in the same row and column get incremented by 1. If the cell was empty, it should now have a value of 1. After each interaction, the affected cells should flash yellow for a brief period.

A secondary trait of the interactive grid is to identify the Fibonacci sequence. If the application detects 5 consecutive numbers from the Fibonacci sequence either horizontally or vertically, the cells carrying those values will flash green briefly before their values are cleared out.

### :link: Potential Backend Endpoints Needed:

- POST /grid: Creates the base 50x50 grid.
- GET /grid: Get the grid.
- POST /grid/cell: Set the state (or value) on a clicked cell and returns the updated state of the grid.
- GET /grid/cell: Retrieves the state of a particular cell.
- PUT /grid/cell: Updates cell value and grid based on clicked cell, and identifies adjacent Fibonacci sequences.
- DELETE /grid/cell: Clears the cells containing the Fibonacci sequence.

---

:point_up: *Please note that in this application the heavy lifting of identifying patterns and modifying the grid will typically occur on the backend. The frontend would primarily focus on dispatching the correct requests and effectively rendering the changing state of the grid.*

:alarm_clock: We fully understand that many of our candidates have significant commitments and may not be able to dedicate extensive time to this coding challenge. If you find yourself constrained for time, please do not hesitate to prioritize those features and aspects of the application that best showcase your strengths, skills, and areas of expertise. In that case, feel free to list and explain potential enhancements.


### Solution: 
- Backend implementation will be directly available in `src` directory and frontend is in `frontend` directory
- To run backend just open in any IDE, and run `GridApplication` in `src/main/kotlin/org/example/grid/GridApplication.kt` spring boot application
- Postgres DB will be run through docker-compose
- To run Angular frontend, go to frontend and execute `npm i` and `npm run start`

### Improvements
- Write frontend unit tests and increase the coverage of backend tests (If I got time today, I will write more scenario based tests)
- Optimise the threading for Fibonacci sequence detection in the grid
- Improve logging
- Toast messages can be displayed on UI when action is performed
- Add rate limiter to avoid abuse of system resources
- Authentication
- Application should be run through docker-compose
- API documentation (OpenAPI Specification)
- Improve the styling of UI pages
