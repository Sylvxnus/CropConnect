# Test Plan for CropConnect System

## Overview
This test plan covers both unit testing conducted throughout system development and user testing to be conducted on external users' post-development.

## Unit Testing
Each developer writes tests for their own components as they build them. Tests must be run and passed before any merges to the main branch of the project GitHub; this is mitigating any pull requests that result in flawed or erroneous code being pulled further into later development stages.

For unit testing, we are going to be ensuring that we are testing every single operation, function and boundaries set. In this document, I am going to set out test cases for our predicted functionality, bear in mind that this might not be all of the tests we need to conduct, so if it gets to the testing phase and there are more, then simply state this.

---

## Test Cases

### Credits Calculation

| Description | Input | Expected Output |
|---|---|---|
| Standard donation entry | 2.0kg | 20 credits |
| Zero quantity | 0.0kg | 0 credits |
| Negative quantity | -1.0kg | IllegalArgumentException |
| Fractional quantity | 0.5kg | 5 credits |
| Large donation | 1000.0kg | IllegalArgumentException |
| Null Input | Null | NullPointerException |

> **Notes:** We need to discuss the limits we are going to implement for the following:
> - Potentially setting a donation limit, this is to ensure that the food banks can cope with high loads, for example, if 30,000 allotments produce a surplus of 10 carrots, 300,000 carrots of surplus total, distributed evenly amongst the 100 food banks in Birmingham = 3,000 carrots per bank, approximately 300kgs worth
> - Setting a credit cap for allotment holders, for example, if we are to give out unlimited credits, people will get the allotments for free, this is bad, potentially limit it to 50 credits a month
> - We also need to discuss what the credits are worth, 10 credits = £1 maybe or something like that and then subsequently how many credits we assign per kg of food donated
> - We need all of this information since we would be pitching this idea to local governments and constituencies, we need to have our ideas in order

---

### Donation Validation

| Description | Input | Expected Output |
|---|---|---|
| Valid Donation | All Fields Entered Correct | True |
| Empty Food Name | " " | False |
| Null Food Name | Null | False |
| Zero Amount | 0.0 | False |
| Negative Amount | -5.0 | False |
| Invalid prod_id | -1 | False |
| Invalid fb_id | -1 | False |
| Null Expiry date | Null | False |
| Expiry date (Past) | Yesterday | False |
| Expiry date (Present/Future) | Today/Future Date | True |

> **Notes:**
> - There is no need to test for the storage requirements variable, as this is going to be selected by the user from a drop-down menu. That way, there is no potential way for the user to enter an erroneous value.

---

### Haversine (Map Calculation for Distance)

| Description | Input | Expected Output |
|---|---|---|
| Valid distances | Ladywood to city centre | Less than 2km |
| Same location | Same coords both sides | 0.0 |
| Symmetrical Distances | A -> B == B -> A | Equal Values |
| Invalid Latitude (High) | 91.0 | IllegalArgumentException |
| Invalid Latitude (Low) | -91.0 | IllegalArgumentException |
| Invalid Longitude (High) | 181.0 | IllegalArgumentException |
| Invalid Longitude (Low) | -181.0 | IllegalArgumentException |

> **Notes:**
> - We also need to have a discussion about why we are using the haversine library, this is because there are already alternatives that we can make use of that would be more viable for the time period. For example, we could potentially make use of an API that does these calculations for us.
> - However, because we are second year students, the brief expects more from us this year, calculating the distance based on given coordinates, would likely further our marks and make us understand the project better.

---

### QR Code / File Output Generation for Credit Redemption

| Description | Input | Expected Output |
|---|---|---|
| Valid redemption generates QR | Valid Transaction | QR code string returned |
| QR code is unique | Two redemptions | Different QR codes |
| Used QR rejected | Already Used QR | False |
| QR linked to correct producer | Prod_id 1 | Returns prod_id 1 |
| Insufficient credits rejected | 0 credits, redeem 10 | IllegalStateException |
| Null transaction id rejected | Null | NullPointerException |
| Expired QR rejected | 31 days old | False |
| PDF generated successfully | Valid redemption | PDF file created with QR |
| PDF QR matches database QR | Generated PDF | QR in PDF matches stored code |
| PDF contains correct details | Valid redemption | Name, value, expiry correct |
| PDF invalid if credits expired | Expired redemption | PDF not generated |

> **Notes:**
> - We want to have more than one option for this allowing for the user to download the QR both digitally (if they have a phone that can display QR codes) and also a file download, where if the user is making use of a device that cannot display QR codes, they can download a file (still with the QR information inside) and redeem their credits that way. (This should hopefully cater to the older generation).

---

### Authentication

| Description | Input | Expected Output |
|---|---|---|
| Valid Credentials | Correct email and password and matches both confirm password and regex validation | Success |
| Wrong Password | Correct email, wrong password | Failure |
| Non-Existent Email | Unknown Email | Failure |
| Empty Email | " " | IllegalArgumentException |
| Empty Password | " " | IllegalArgumentException |
| Confirm Password doesn't match | Correct email, confirm password doesn't match | Failure |
| SQL injection Email | ' OR 1=1 -- | Failure, No Breach |
| SQL injection Password | ' OR 1=1 -- | Failure, No Breach |
| SQL injection Confirm Password | ' OR 1=1 -- | Failure, No Breach |
| Null email | Null | NullPointerException |
| Password validation | Incorrect Password, doesn't match Regex | Failure |

---

### Food Bank Map Filter

| Description | Input | Expected Output |
|---|---|---|
| No referral filter | Referral = false | Only no referral banks |
| Open now filter | Current Time | Only currently open banks |
| Fresh produce filter | hasFreshProduce = true | Only banks with available produce |
| Combined filters | Referral = false, Open = true | Food banks matching both conditions |
| No results | Filters with no matches | Empty list returned not crash |
| Null filter applied | Null | All food banks returned |
| All filters combined | Referral = false, Open = true, Produce = true | Food banks matching all three |
| Filter then searches | Filter applied then search entered | Search results respect active filters |

> **Notes:**
> - We also need to do a little bit of research into food banks that actually have no referral requirements, as they are likely few and far between. We are also not allowing for food banks to manually create their own accounts, we are creating their accounts for them, giving them a password ourselves.

---

### Search Tests

| Description | Input | Expected Output |
|---|---|---|
| Valid Product Search | "carrot" | Food banks currently stocking carrots are returned |
| Case insensitive searching | "Carrot" | Same result as lower-case carrot |
| Partial Search term | "Carr" | Returns carrot |
| Empty search term | " " | All food banks returned |
| Null search term | Null | All food banks returned |
| Search with active filter | "Carrot" + referral = false | Only no referral food banks that are currently stocking carrots |
| Search special characters | "Carrots!!" | Special characters stripped, valid result returned |
| Search SQL injection | ' OR 1=1 -- | Sanitised, no breach, empty results |
| Search very long string | 500-character input | IllegalArgumentException |
| Clear search restores map | Search cleared | All pins restored on the map |
| Search unavailable item | "bread" with no stock at any bank | Empty list with appropriate message |

> **Notes:**
> - Have a look and see if we can implement Aidan's search engine from his Information retrieval module here, it is in python, but we might be able to link it.

---

### Other

*Add to this later if we think of any more tests for the system*

| Description | Input | Expected Output |
|---|---|---|
| Language change | User clicks on their respective language | All important aspects of the application should be refactored into that given language (Urdu, Punjabi, Bengali) |
| Weather API Shown | Current date of the year in regard to the season | Should display advice to the allotment producer about what crops they should be growing |

---

## User Testing

This will be conducted on external users after a working prototype is completed, we need to ideally have 3 participants. Erin Hassell, Colin Wood, and Jed Sam, as these cases have varying technical abilities, alike that of what the project would undergo in the real world with age demographics.

We must analyse them using the entire system, working through each feature as any user would:

- **Erin** will be assigned as a regular user, someone with little technical knowledge who is using the application to find food banks
- **Colin** will be assigned as the allotment producer, who is going to be logging donations and using the weather features to plan her allotment growth
- **Jed** will be assigned as the food bank coordinator, as this is likely the person with the most technical expertise, he will be tasked with updating their stock of food, quantities and other elements to that side of the program

We will then get feedback on the application based on the following factors rated out of 10:

- Ease of use and navigation
- Looks and Aesthetics
- Functionality and Features
- System Feedback and Error handling
- Key Quotes and Other Feedback

We will then display these results as a matrix or graphically, we can decide this later.

Then we can use this data to decide on the next steps to take our project to market and improve the system.

---

*Aidan Wood*
