# RemindMeLater
## Design Document
### Connor Cook, Christopher Elbe, Steven Falconieri, Christian Grothaus 

## Introduction 
How often do you run out of a product, or realize you needed a specific item from a store? This happens to us quite often: we realize we need something and think, “I should get that the next time I am at the store” only to forget that we needed it when we get there. RemindMeLater aims to allow users to set reminders and be notified at the right time. If you realize you need paper towels the next time you are at Kroger, you can set a reminder for the location of your choosing. The next time you are within a certain radius of Kroger, you will be notified to purchase paper towels.  

We also want to allow multiple users to join groups and send reminders to each other. For example, if I am riding in the car with my dad while charging my phone, I may forget to grab my charger before leaving. If this is the case, I can set a reminder for my dad’s phone to notify him the next time he gets to my house. His phone’s onboard navigation will recognize the pre-determined location that I set and notify him of my note the next time he is within a certain radius of my house.  

## Storyboard 
[RemindMeLater Storyboard](https://projects.invisionapp.com/prototype/ckyz5anql0004z501i6op35w0/play)


![StoryboardMain](https://user-images.githubusercontent.com/26448642/151712166-10061b3d-7f2c-4638-bc32-113db4d55f9b.png)
![Storyboard2](https://user-images.githubusercontent.com/26448642/151712175-28c628ff-390a-4ac2-8444-20f44ffa364d.png)
![Storyboard3](https://user-images.githubusercontent.com/26448642/151712184-24ce892a-1602-4927-ba63-3f8cbb9747a6.png)

- On the first screen, a map is displayed which will pinpoint your current reminders 
- On the second screen, users can enter reminders for themselves – only they will receive these reminders 
- On the third screen, users can enter other types of reminders, and for other users in their groups 

![StoryboardMain](https://user-images.githubusercontent.com/26448642/151712191-523c4e74-a8c9-4718-a101-2a3aeb270870.png)
- Users can also manage their current reminders, edit and remove them.  

## Functional Requirements 

**Given** a user wants to set a reminder to purchase paper towels the next time they enter Kroger  

**When** a user sets a new reminder 

**Then** the user will set the reminder to Kroger’s location, and add the note “Purchase Paper Towels”
<br/><br/> 

  
**Given** Kroger is located at 123 S Main St. 

**When** The user is within 10 yards of 123 S Main St.  

**Then** A reminder will display  
<br/><br/>  
 
**Given** you want to tell your mother a story about work, the next time you see her 

**When** you arrive at your mother’s house the next day  

**Then** a notification reminds you to tell your mother the story  
<br/><br/> 
    
**Given** a reminder displays on the user’s device  

**When** the user interacts with the notification  

**Then** the user can either cancel or repeat the reminder  
<br/><br/> 
  
**Given** a family of four are in a reminder group  

**When** a family member adds a reminder to pick up milk at Kroger  

**Then** all family members receive a notification the next time they enter Kroger  
<br/><br/> 
  
**Given** You left your charger in your dad’s car 

**When** you set a reminder for the next time your dad comes to your house 

**Then** when your dad arrives at your house, a reminder will notify him to return your charger 
<br/><br/> 
   
## Class Diagram 
![ClassDiagram](https://user-images.githubusercontent.com/26448642/151712249-1223edec-e8d8-434a-840d-95da18b3d050.png)

The class diagram consists of 5 packages: a main package, UI package, service package, DTO package and DAO package.  Within the main package we have the class “AppModule” for Koin for implementing dependency injection.  Also, within the main package we will have a class called “RetrofitClientInstance”.  This class will be used for being able to parse JSON files.  The next package, UI, will consist of “MainActivity”, “ViewModel”, and “ApplicationViewModel” classes.  The “MainActivity” class will be used with the “ViewModel” and “ApplicationViewModel” to display live data.  The service package will contain “IReminderService” which will be an interface used to for both “ReminderService” and “ReminderServiceStub”.  “ReminderService” will pass any DAOs to the “ViewModel”.  The stub will be added to allow for UI work to be done even without the “ReminderService” being completed.  In the DTO package we will have a “Reminder” class.  This class will be used for creating objects that hold a unique reminder ID, the thing you want to be reminded of, the location of the reminder, alert radius, and who to remind.  The DAO package contains a “IReminderDAO” class that will be used by “ReminderService” to access stored data.  There will also be a “ReminderDatabase” class which will extend a Room database for storing the information for the reminders locally. 
 

## Product Backlog 
[Backlog for Design Stage](https://github.com/cook2c9/RemindMeLater/projects)

![Backlog](https://user-images.githubusercontent.com/26448642/151712274-9687903e-c142-4bf5-a43c-c54963e35413.png)

## Scrum/Kanban Board 
[Kanban Board](https://github.com/cook2c9/RemindMeLater/projects/1)

![KanbanBoard](https://user-images.githubusercontent.com/26448642/151712282-f4742639-7c43-4e1e-9f09-6d349d1efeee.png)

## Scrum Roles 
Product Owner – Connor Cook
<br/>
SCRUM Master – Connor Cook
<br/>
Developers - Christopher Elbe, Steve Falconieri, Christian Grothaus
<br/>

## Link to meeting 
Meeting via Teams – Created a separate group with all members included. We also created a group chat via text, so we can quickly communicate 
Github - https://github.com/cook2c9/RemindMeLater 
