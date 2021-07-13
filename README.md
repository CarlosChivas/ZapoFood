# ZapoFood

## Table of Contents
1. [Overview](#Overview)
1. [Product Spec](#Product-Spec)
1. [Wireframes](#Wireframes)
2. [Schema](#Schema)

## Overview
### Description
This application has the purpose of allowing users to make reservations at their favorite restaurants near their locations, the restaurants will be rated in the application according to the opinions of the users.
The user will be able to add restaurants to favorites, this section will save the option of favorite restaurants for the user, this will allow the user to make the reservations that he makes daily in a fast way.

### App Evaluation
- **Category:** Food / Service
- **Mobile:** Focused on mobile experience, uses the location system
- **Story:** The application allows users to reserve a place in a restaurant from their homes.
- **Market:** Anyone user who want to avoid spend time looking for a place to eat
- **Habit:** The user can navigate through the application looking for his favorite restaurants, he can also see the opinions of the users about those places trying to make a better decision.
- **Scope:** We want to start focused on offering a list of options that the user can use to make a reservation and enjoy a little food without having to be afraid of finding a good place with available places, over time we can improve with functionalities such as offering recommendations according to the user's tastes.

## Product Spec

### 1. User Stories (Required and Optional)

**Required Must-have Stories**

* The user can log into the application with different account options such as Facebook, Google or ZapoFood itself. 
* The application will display different restaurant options near the user's location.
* The user can select any option and the application will show the places available to reserve.
* User can select the time for the reservation and the duration.
* Search bar for find quickly their favorites restaurants.
* The user can see his reservations

**Optional Nice-to-have Stories**

* Have an administration interface for restaurant owners and have updates on the places in their restaurants to be able to make reservations according to the requests made.
* The reservation be registered into a database and removed from the list of places availables for this restaurant in the app.
* Need a significant amount of money to make the reservation which will be returned at the end of your reservation in case you have attended, this with the purpose of preventing them from making reservations and not having the commitment to attend, thus we help restaurants to not generate losses for reservations that will not be used

### 2. Screen Archetypes

* Login
   * The user can log into the application with different account options such as Facebook, Google or ZapoFood itself. 
* Stream
   * The application will display different restaurant options near the user's location.
   * Search bar for find quickly their favorites restaurants.
* Detail
    * The user can select any option and the application will show the places available to reserve.
    * User can select the time for the reservation and the duration.
* My reservations
    * The user can see his reservations

### 3. Navigation

**Tab Navigation** (Tab to Screen)

* Stream Screen
* My reservations
* Logout

**Flow Navigation** (Screen to Screen)

* Login Screen
   * Stream Screen
* Stream Screen
   * Make reservation
* Make reservation
    * My reservations

## Wireframes


### Digital Wireframes & Mockups
<img src="https://github.com/CarlosChivas/ZapoFood/blob/master/Images/Android%20-%201.png" width=300><img src="https://github.com/CarlosChivas/ZapoFood/blob/master/Images/Android%20-%202.png" width=300><img src="https://github.com/CarlosChivas/ZapoFood/blob/master/Images/Android%20-%203.png" width=300>


### Interactive Prototype

## Schema 
### Models
#### Restaurant

   | Property      | Type     | Description |
   | ------------- | -------- | ------------|
   | objectId      | String   | Unique id for the user post (default field) |
   | name          | String   | Restaurant name |
   | image         | File     | Image o logo of the restaurant |
   | description       | String   | Restaurant description |
   | score | Number   | Score determinated by users |
   | createdAt     | DateTime | Date when restaurant is created (default field) |
   | updatedAt     | DateTime | Date when restaurant is last updated (default field) |
### Networking
- [Add list of network requests by screen ]
- [Create basic snippets for each Parse network request]
- [OPTIONAL: List endpoints if using existing API such as Yelp]
