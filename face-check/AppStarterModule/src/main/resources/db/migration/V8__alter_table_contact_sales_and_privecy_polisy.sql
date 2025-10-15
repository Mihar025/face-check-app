
CREATE TABLE contact_sales_form(
                                   id SERIAL PRIMARY KEY,
                                   firstName VARCHAR(255),
                                   lastName VARCHAR(255),
                                   phoneNumber VARCHAR(255)
);

CREATE TABLE terms_of_use_agreement (
                                        id SERIAL PRIMARY KEY,
                                        event VARCHAR(255),
                                        userId INT,
                                        timeStamp DATE,
                                        termsVersion VARCHAR(255),
                                        privacyVersion VARCHAR(255),
                                        ip VARCHAR(255),
                                        device VARCHAR(255),
                                        osVersion VARCHAR(255)
);

