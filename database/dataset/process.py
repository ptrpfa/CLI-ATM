""" Configurations """
# Imports
import os, re, json
import pandas as pd
import pickle

# # Google Colab
# from google.colab import drive
# drive.mount('/content/drive/')
# %cd /content/drive/My Drive/Colab Notebooks/

# Settings
# Files
filepath = "files"
dataset_file = "%s/excel/bank.xlsx" % filepath
output_dataset_file = "%s/excel/processed_bank.xlsx" % filepath
output_accounts_file = "%s/excel/accounts.xlsx" % filepath
pklfile_accounts = "%s/pickles/accounts.pkl" % filepath
pklfile_dataset = "%s/pickles/dataset.pkl" % filepath

# Columns
column_mappings = { 
                    "Account No": "AccountNo", 
                    "DATE": "ValueDate", 
                    "TRANSACTION DETAILS": "Remarks", 
                    "CHQ.NO.": "ChequeNo", 
                    "VALUE DATE": "Date",
                    "WITHDRAWAL AMT": "Credit", 
                    "DEPOSIT AMT": "Debit", 
                    "BALANCE AMT": "Balance"
                }
column_order = ["TransactionNo", "AccountNo", "ChequeNo", "Date", "ValueDate", "Debit", "Credit", "Status", "Balance", "Remarks"]

# Functions
# Function to pickle object (accepts object to pickle and its filename to save as)
def pickle_object (pickle_object, filepath):
    # Create file object to store object to pickle
    file_pickle = open (filepath, 'wb') # w = write, b = bytes (overwrite pre-existing files if any)

    # Pickle (serialise) object [store object as a file]
    pickle.dump (pickle_object, file_pickle)

    # Close file object
    file_pickle.close ()

# Function to load pickle object (accepts filename of pickle to load and returns the de-pickled object)
def load_pickle (filepath):
    # Create file object accessing the pickle file
    file_pickle = open (filepath, 'rb') # r = read, b = bytes

    # Get pickled object
    pickled_object = pickle.load (file_pickle)

    # Close file object
    file_pickle.close ()

    # Return pickle object
    return pickled_object

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

# Create TransactionNo column
df_dataset['TransactionNo'] = df_dataset.index + 1

# Create Status column (set all transactions to be successful)
df_dataset['Status'] = 1

# Rename columns
df_dataset.rename(columns = column_mappings, inplace = True)

# Rearrange columns
df_dataset = df_dataset[column_order]

""" Data Processing """
# Remove trailling '
df_dataset['AccountNo'] = df_dataset['AccountNo'].str.strip("'")

# Format dates
df_dataset['Date'] = df_dataset['Date'].dt.strftime("%d/%m/%Y")
df_dataset['ValueDate'] = df_dataset['ValueDate'].dt.strftime("%d/%m/%Y")

# Loop through each row to update the formatting of account, transaction and cheque numbers 
for index in df_dataset.index:
  # AccountNo formatting
  if(len(df_dataset.loc[index]['AccountNo']) == 7): #  Check length of account no
      df_dataset.at[index, 'AccountNo'] = "40900" + df_dataset.loc[index]['AccountNo']

  # TransactionNo formatting
  df_dataset.at[index, 'TransactionNo'] = "{:0>8}-{}-{}".format(df_dataset.loc[index]['TransactionNo'], re.search("^\d{2}\/(\d{2})\/(\d{4})$", df_dataset.loc[index]['Date']).group(1), re.search("^\d{2}\/(\d{2})\/(\d{4})$", df_dataset.loc[1]['Date']).group(2))

  # ChequeNo formatting
  if(not pd.isna(df_dataset.loc[index]['ChequeNo'])):
    df_dataset.at[index, 'ChequeNo'] = "{:0>6}:{}:{}".format(int(df_dataset.loc[index]['ChequeNo']), df_dataset.loc[index]['Date'].replace("/", "")[2:], df_dataset.loc[index]['AccountNo'])

""" Anomaly Handling """
# Swap Date and ValueDate values of record with TransactionNo = 00063319-05-2017
df_dataset.loc[df_dataset['TransactionNo'] == "00063319-05-2017", "Date"], df_dataset.loc[df_dataset['TransactionNo'] == "00063319-05-2017", "ValueDate"] = df_dataset.loc[df_dataset['TransactionNo'] == "00063319-05-2017", "ValueDate"], df_dataset.loc[df_dataset['TransactionNo'] == "00063319-05-2017", "Date"]

# Set status of suspected fraudulent transactions to failed
df_dataset.loc[df_dataset['TransactionNo'] == "00002991-02-2017", "Status"] = -1
df_dataset.loc[df_dataset['TransactionNo'] == "00013593-02-2017", "Status"] = -1
df_dataset.loc[df_dataset['TransactionNo'] == "00033982-05-2017", "Status"] = -1
df_dataset.loc[df_dataset['TransactionNo'] == "00035707-12-2017", "Status"] = -1
df_dataset.loc[df_dataset['TransactionNo'] == "00035966-02-2017", "Status"] = -1
df_dataset.loc[df_dataset['TransactionNo'] == "00036366-04-2017", "Status"] = -1


""" Balance Calculations """
# Reset balance
df_dataset['Balance'] = 0

# Create Accounts dataframe
df_accounts = df_dataset[['AccountNo', 'Balance']].copy()   # Copy specified columns
df_accounts.drop_duplicates(inplace = True)                 # Drop duplicates
df_accounts.index = range(len(df_accounts.index))           # Reset index

# Loop through each row to re-calculate balances
for index in df_dataset.index:
    # Get current balance
    current_balance = df_accounts.loc[df_accounts['AccountNo'] == df_dataset.loc[index]['AccountNo'], 'Balance'].values[0]

    # Check if transaction status is successful
    if(df_dataset.loc[index, "Status"] == 1):
      # Check if transaction is a Credit transaction (outflow of money)
      if(pd.isna(df_dataset.loc[index]['Debit'])):
          # Update current balance
          current_balance -= df_dataset.loc[index]['Credit'] 
      # Transaction is a Debit transaction (inflow of money)
      else: 
          # Update current balance
          current_balance += df_dataset.loc[index]['Debit']
    
    # Update balances
    df_accounts.loc[df_accounts['AccountNo'] == df_dataset.loc[index]['AccountNo'], 'Balance'] = current_balance    # Update account dataframe's balance
    df_dataset.at[index, 'Balance'] = current_balance                                                               # Update current row's balance

""" Pickle objects and export dataframes """
# Export dataframe to excel
df_dataset.to_excel(output_dataset_file)
df_accounts.to_excel(output_accounts_file)

# Pickle dataframe objects
pickle_object(df_dataset, pklfile_dataset)
pickle_object(df_accounts, pklfile_accounts)