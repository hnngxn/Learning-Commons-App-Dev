**1. Title and Group Members**
* Title: Learning Commons Cloud System
* Team Members: Han Nguyen, Saurav Poudel, Uyen Duong, Van Diep, Jaxon Zaleski    

**2. Project Overview**
* We've been asked to revamp the Learning Commons Cloud System, it is an outdated system that has some annoyances, such as not getting a confirmation to a submition. This leads to multiple emails getting sent out to the supervisor and user, and the user going back to subtract student points.

**3. Goals and Objectives**
* This project will resemble the previous application it’s based on in many ways, but it will have additional features designed on new code/hardware. The newer features include confirmation of text for form submission, the ability to add multiple students to the form at a time, and others.

**4. Functional Requirements**

* Bulk Intake Form
	* Given multiple new tutors need to be added to the system at the beginning of a semester
	* When an administrator uploads a bulk intake file
	* Then the system should create records for all valid tutor entries

* Raise Eligibility Notification
	* Given a tutor has accumulated performance points
	* When the tutor reaches 125 or more points
	* Then the system should automatically notify relevant staff that the tutor is eligible for a raise

* Supervisor Notifications
	* Given a point transaction is entered for a tutor
	* When the transaction is submitted
	* Then the system should send notifications to the employee, supervisor, and staff member who entered the points

* Semester Reset
	* Given a new semester is starting
	* When an administrator performs a semester reset
	* Then tutor point totals should be reset according to established rules

* Point Reversal
	* Given a point entry was entered incorrectly
	* When an administrator chooses to reverse the transaction
	* Then the tutor's point total should be adjusted accordingly

* Positive Infraction Entry
	* Given an employee receives an infraction
	* When a staff member enters the infraction value as a positive number
	* Then the system should subtract the value from the tutor's rolling point total

* Employee Setup During Semester Reset
	* Given the system is being prepared for a new semester
	* When an administrator completes the semester reset process
	* Then new tutors should receive 65 starting points and returning tutors should receive 70 starting points

* Staff Maintenance
	* Given staff membership changes between semesters
	* When an administrator adds or removes tutors from the system
	* Then the database should accurately reflect the current roster of tutors

* Automated Email Distribution
	* Given a point-related event requires notification
	* When the system generates an automated email
	* Then the email should be sent to all designated recipients based on the notification rules

* Training Resources
	* Given a new user needs assistance learning the system
	* When the user accesses training materials
	* Then the user should be able to view process documentation and instructional resources for system usage

**5. Storyboard (screen mockups):**

* The storyboard illustrates the primary workflows of the Learning Commons Staff Points System. The application includes a standard staff-points entry workflow and additional administrative workflows.

**Staff Points Entry Workflow**

Home → Who and What → Bonus/Infraction Details → Points and Date → Review → Confirmation

The user begins by selecting a staff member and choosing whether to record a bonus or infraction. The user then selects the event type, reviews the point value and date, adds any required details or attachments, and reviews the entry before submitting. The system displays a confirmation after the entry is successfully saved.

**Administrative Workflows**

- **Bulk Intake** : Apply the same event to multiple staff members.
- **Eligibility Dashboard** : View staff point totals and raise eligibility.
- **Point Reversal** : Reverse an incorrect point entry while preserving the history.
- **Semester Reset** : Manage the roster and reset staff points for a new semester.

**Screen Mockups**

[View Screen Mockups](./docs/UCLCMockups.pdf)

**6. Class Diagram**

<img width="3144" height="4108" alt="LC UML DIagram" src="https://github.com/user-attachments/assets/15003a8d-75c4-4e48-a466-6a7a31996ba6" />

* UML-based class diagram. 

* Class Diagram Description: One or two lines for each class to describe use of interfaces, classes and resources, interfaces, etc. Don't worry about putting more than a few words for each class; this does not need to be thorough. 
You need to submit both a document file and a link to this .md file on your GitHub repo.


**7. Architecture and components of your application (Diagram)**
* ![Architecture Diagram](docs/architecture.png)

**8. Scrum roles and who will fill those roles (responsibilities of each member in the group)**
* Van Diep
	* Role: Scrum Master
 	* Responsibilities: Leads Scrum meetings, coordinates Sprint planning, removes blockers.	
* Saurav Poudel
	* Role: Developer
 	* Responsibilities: Backend and front-end development
* Han Nguyen
 	* Role: Developer
 	* Responsibilities: Front-end developer, and help with back-end coding	
* Uyen Duong
 	* Role: Developer
 	* Responsibilities:		
* Jaxon Zaleski
 	* Role: Developer
 	* Responsibilities: Back-end and front-end if needed
  
**9. GitHub project [link.](https://github.com/hnngxn/Learning-Commons-App-Dev/projects)** 

**10. Each team must submit their GitHub repository and GitHub Project board. This will be used to track milestones, stories, and sprint tasks for your final project. Set it up as follows:**
This project will resemble the previous application it’s based on in many ways, but it will have additional features designed on new code/hardware. The newer features include confirmation of text for form submission, the ability to add multiple students to the form at a time, and others.
