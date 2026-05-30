<img width="988" height="683" alt="image" src="https://github.com/user-attachments/assets/dfcf5866-11fa-454d-8d7d-912bb513f195" />




Code For the ERDs 

//This is the table for where we store all
//Info about the allotment producers
Table crop_producers {
  prod_id integer [pk, increment]
  prod_name varchar
  prod_email varchar unique
  prod_password varchar
}
//table for storing the upcoming 
Table donations {
  donation_id integer [pk, increment]
  prod_id integer
  fb_id integer
  donation_amount float
  food_name varchar
  donation_date date
  storage_req text
  expiry_date date
}
Table food_bank_products {
  fb_id integer
  product_id integer [pk, increment]
  product_name varchar
  product_quant integer
  upcoming_donation integer  
}
table food_bank {
  fb_id integer [pk, increment]
  fb_name varchar
  fb_email varchar
  fb_phone varchar unique
  fb_long decimal(9,6)
  fb_lat decimal(9,6)
  fb_password varchar
}

Table credits_transactions {
  transaction_id integer [pk, increment]
  prod_id integer
  donation_id integer
  credit_val integer
  transaction_type varchar
  created_at date
}


Table redemptions {
  redemption_id integer [pk, increment]
  transaction_id integer
  prod_id integer
  qr_code varchar unique
  is_used boolean
  created_at date
  used_at date
}


Ref: donations.prod_id > crop_producers.prod_id
Ref: donations.fb_id > food_bank.fb_id
Ref: credits_transactions.donation_id > donations.donation_id
Ref: credits_transactions.prod_id > crop_producers.prod_id
Ref: food_bank_products.fb_id > food_bank.fb_id
Ref: redemptions.transaction_id > credits_transactions.transaction_id
Ref: redemptions.prod_id > crop_producers.prod_id
