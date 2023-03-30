### Financial Data Processing
#### Files and Components
```
README.md (this file)

requirements.txt (program requirements)

process.py (main data processing program)

adhoc_process.py (supplementary program for further processing of data on the database, should not be run)

files/ 
    excel/ (contains all raw and processed exel files)
    pickles/ (contains all pickled objects)
    cleaned_bank.xlsx (final consolidated cleaned dataset)
```
The dataset has already been pre-processed at this stage. However, you can follow these instructions, if you want to re-process the data again:
1. Ensure that you have `python3` and `pip3` installed on your machine. Click [here](https://www.python.org/downloads/) for installation instructions if they are not already installed.
2. Install the necessary program dependencies for this project by running the following command on a terminal:
    ```
    pip3 install -r requirements.txt
    ```
    **Ensure that you are in the same working directory as the program files*
3. Run `process.py` on a terminal, ensuring that the raw `bank.xlsx` dataset file is present in `files/excel`. The processing will take a while. Once the program has finished execution, the cleaned excel files will be saved under `files/excel/accounts.xlsx` and `files/excel/processed_bank.xlsx`. Dataframes (`pandas` library) for the financial data are also saved into pickled files, via serialisation, which is a method of saving program objects into loadable byte stream files. 
    ```
    python3 process.py
    ```
2. This step is optional and should not be run as it is used for the further processing of the dataset on the database (it assumes the dataset is already migrated into the database, which we have already done previously). Regardless, to run `adhoc_process.py`, type the following command on a terminal:
    ```
    python3 adhoc_process.py
    ```