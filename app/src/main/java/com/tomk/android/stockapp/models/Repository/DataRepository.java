package com.tomk.android.stockapp.models.Repository;

import java.util.ArrayList;

public class DataRepository {
    private ArrayList<RepositoryItem> repository;

    public DataRepository()
    {
        repository = new ArrayList<>();
        repository.add(new RepositoryItem("AXP" , "American Express Co"));
        repository.add(new RepositoryItem("AAPL", "Apple Inc"));
        repository.add(new RepositoryItem("BA" ,  "Boeing Co"));
        repository.add(new RepositoryItem("CAT" , "Caterpillar Inc"));
        repository.add(new RepositoryItem("CSCO", "Cisco Systems Inc"));
        repository.add(new RepositoryItem("CVX" , "Chevron Corp"));
        repository.add(new RepositoryItem("DIA" , "Dow Industrial Average"));
        repository.add(new RepositoryItem("DWDP", "DowDuPont Inc"));
        repository.add(new RepositoryItem("XOM" , "Exxon Mobil Corp"));
        repository.add(new RepositoryItem("GS" ,  "Goldman Sachs Group Inc"));
        repository.add(new RepositoryItem("HD" ,  "Home Depot Inc"));
        repository.add(new RepositoryItem("IBM" , "International Business Machines Corp"));
        repository.add(new RepositoryItem("INTC", "Intel Corp"));
        repository.add(new RepositoryItem("JNJ" , "Johnson & Johnson"));
        repository.add(new RepositoryItem("KO" ,  "Coca-Cola Co"));
        repository.add(new RepositoryItem("JPM" , "JPMorgan Chase & Co"));
        repository.add(new RepositoryItem("MCD" , "McDonald's Corp"));
        repository.add(new RepositoryItem("MMM" , "3M Co"));
        repository.add(new RepositoryItem("MRK" , "Merck & Co Inc"));
        repository.add(new RepositoryItem("MSFT","Microsoft Corp"));
        repository.add(new RepositoryItem("NDAQ", "NASDAQ Inc"));
        repository.add(new RepositoryItem("NKE","Nike Inc"));
        repository.add(new RepositoryItem("PFE" ,"Pfizer Inc"));
        repository.add(new RepositoryItem("PG","Procter & Gamble Co"));
        repository.add(new RepositoryItem("SPY" , "Standard and Poor 500"));
        repository.add(new RepositoryItem("TRV","Travelers Companies Inc"));
        repository.add(new RepositoryItem("UNH","UnitedHealth Group Inc"));
        repository.add(new RepositoryItem("UTX","United Technologies Corp"));
        repository.add(new RepositoryItem("VZ","Verizon Communications Inc"));
        repository.add(new RepositoryItem("V","Visa Inc"));
        repository.add(new RepositoryItem("WBA","Walgreens Boots Alliance Inc"));
        repository.add(new RepositoryItem("WMT","Walmart Inc"));
        repository.add(new RepositoryItem("DIS","Walt Disney Co"));
    }

    public ArrayList<RepositoryItem> getRepository() {
        return repository;
    }

    public void setRepository(ArrayList<RepositoryItem> repository) {
        this.repository = repository;
    }
}
