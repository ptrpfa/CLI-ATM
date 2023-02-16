""" Configurations """
# Imports
import os, re, json
import pandas as pd

# # Google Colab
# from google.colab import drive
# drive.mount('/content/drive/')
# %cd /content/drive/My Drive/Colab Notebooks/

# Settings
dataset_file = "files/bank.xlsx"
column_mappings = { 
                    "Account No": "Account", 
                    "DATE": "ValueDate", 
                    "TRANSACTION DETAILS": "Remarks", 
                    "CHQ.NO.": "ChequeNo", 
                    "VALUE DATE": "Date",
                    "WITHDRAWAL AMT": "Credit", 
                    "DEPOSIT AMT": "Debit", 
                    "BALANCE AMT": "Balance"
                }
column_order = ["TransactionID", "Account", "ChequeNo", "Date", "ValueDate", "Debit", "Credit", "Balance", "Remarks"]

""" Program Entrypoint """
# Read dataset file
df_dataset = pd.read_excel(dataset_file)

""" Preliminary Processing """
# Drop . column
df_dataset.drop(axis = 1, columns = ".", inplace = True) 

# Remove duplicate row entries
df_dataset.drop_duplicates(inplace = True)

# Reset index
df_dataset.index = range(len(df_dataset.index))

# Create TransactionID column
df_dataset['TransactionID'] = df_dataset.index + 1

# Rename columns
df_dataset.rename(columns = column_mappings, inplace = True)

# Rearrange columns
df_dataset = df_dataset[column_order]

""" Data Processing """
# Remove trailling '
df_dataset['Account'] = df_dataset['Account'].str.strip("'")

# Format dates
df_dataset['Date'] = df_dataset['Date'].dt.strftime("%d/%m/%Y")
df_dataset['ValueDate'] = df_dataset['ValueDate'].dt.strftime("%d/%m/%Y")

# Reset balance
df_dataset['Balance'] = 0

# Create Accounts dataframe
df_accounts = df_dataset[['Account', 'Balance']].copy()     # Copy specified columns
df_accounts.drop_duplicates(inplace = True)                 # Drop duplicates
df_accounts.index = range(len(df_accounts.index))           # Reset index

# Loop through each row to re-calculate balances
for index in df_dataset.index:
    # Get current balance
    current_balance = df_accounts.loc[df_accounts['Account'] == df_dataset.loc[index]['Account'], 'Balance'].values[0]

    # Check if transaction is a Credit transaction (outflow of money)
    if(pd.isna(df_dataset.loc[index]['Debit'])):
        current_balance -= df_dataset.loc[index]['Credit']                                                          # Update current balance
        df_accounts.loc[df_accounts['Account'] == df_dataset.loc[index]['Account'], 'Balance'] = current_balance    # Update account dataframe's balance
        df_dataset.at[index, 'Balance'] = current_balance                                                           # Update current row's balance

    else: # Transaction is a Debit transaction (inflow of money)
        current_balance += df_dataset.loc[index]['Debit']                                                           # Update current balance
        df_accounts.loc[df_accounts['Account'] == df_dataset.loc[index]['Account'], 'Balance'] = current_balance    # Update account dataframe's balance
        df_dataset.at[index, 'Balance'] = current_balance                                                           # Update current row's balance

# Export dataframe to excel
df_dataset.to_excel("files/processed_bank.xlsx")
df_accounts.to_excel("files/accounts.xlsx")