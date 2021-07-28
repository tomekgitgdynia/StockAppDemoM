package com.tomk.android.stockapp.WebAccess;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;


public class   CompanyOverview implements Serializable
{

    @SerializedName("Symbol")
    @Expose
    private String symbol;
    @SerializedName("AssetType")
    @Expose
    private String assetType;
    @SerializedName("Name")
    @Expose
    private String name;
    @SerializedName("Description")
    @Expose
    private String description;
    @SerializedName("CIK")
    @Expose
    private String cik;
    @SerializedName("Exchange")
    @Expose
    private String exchange;
    @SerializedName("Currency")
    @Expose
    private String currency;
    @SerializedName("Country")
    @Expose
    private String country;
    @SerializedName("Sector")
    @Expose
    private String sector;
    @SerializedName("Industry")
    @Expose
    private String industry;
    @SerializedName("Address")
    @Expose
    private String address;
    @SerializedName("FiscalYearEnd")
    @Expose
    private String fiscalYearEnd;
    @SerializedName("LatestQuarter")
    @Expose
    private String latestQuarter;
    @SerializedName("MarketCapitalization")
    @Expose
    private String marketCapitalization;
    @SerializedName("EBITDA")
    @Expose
    private String ebitda;
    @SerializedName("PERatio")
    @Expose
    private String pERatio;
    @SerializedName("PEGRatio")
    @Expose
    private String pEGRatio;
    @SerializedName("BookValue")
    @Expose
    private String bookValue;
    @SerializedName("DividendPerShare")
    @Expose
    private String dividendPerShare;
    @SerializedName("DividendYield")
    @Expose
    private String dividendYield;
    @SerializedName("EPS")
    @Expose
    private String eps;
    @SerializedName("RevenuePerShareTTM")
    @Expose
    private String revenuePerShareTTM;
    @SerializedName("ProfitMargin")
    @Expose
    private String profitMargin;
    @SerializedName("OperatingMarginTTM")
    @Expose
    private String operatingMarginTTM;
    @SerializedName("ReturnOnAssetsTTM")
    @Expose
    private String returnOnAssetsTTM;
    @SerializedName("ReturnOnEquityTTM")
    @Expose
    private String returnOnEquityTTM;
    @SerializedName("RevenueTTM")
    @Expose
    private String revenueTTM;
    @SerializedName("GrossProfitTTM")
    @Expose
    private String grossProfitTTM;
    @SerializedName("DilutedEPSTTM")
    @Expose
    private String dilutedEPSTTM;
    @SerializedName("QuarterlyEarningsGrowthYOY")
    @Expose
    private String quarterlyEarningsGrowthYOY;
    @SerializedName("QuarterlyRevenueGrowthYOY")
    @Expose
    private String quarterlyRevenueGrowthYOY;
    @SerializedName("AnalystTargetPrice")
    @Expose
    private String analystTargetPrice;
    @SerializedName("TrailingPE")
    @Expose
    private String trailingPE;
    @SerializedName("ForwardPE")
    @Expose
    private String forwardPE;
    @SerializedName("PriceToSalesRatioTTM")
    @Expose
    private String priceToSalesRatioTTM;
    @SerializedName("PriceToBookRatio")
    @Expose
    private String priceToBookRatio;
    @SerializedName("EVToRevenue")
    @Expose
    private String eVToRevenue;
    @SerializedName("EVToEBITDA")
    @Expose
    private String eVToEBITDA;
    @SerializedName("Beta")
    @Expose
    private String beta;
    @SerializedName("52WeekHigh")
    @Expose
    private String _52WeekHigh;
    @SerializedName("52WeekLow")
    @Expose
    private String _52WeekLow;
    @SerializedName("50DayMovingAverage")
    @Expose
    private String _50DayMovingAverage;
    @SerializedName("200DayMovingAverage")
    @Expose
    private String _200DayMovingAverage;
    @SerializedName("SharesOutstanding")
    @Expose
    private String sharesOutstanding;
    @SerializedName("SharesFloat")
    @Expose
    private String sharesFloat;
    @SerializedName("SharesShort")
    @Expose
    private String sharesShort;
    @SerializedName("SharesShortPriorMonth")
    @Expose
    private String sharesShortPriorMonth;
    @SerializedName("ShortRatio")
    @Expose
    private String shortRatio;
    @SerializedName("ShortPercentOutstanding")
    @Expose
    private String shortPercentOutstanding;
    @SerializedName("ShortPercentFloat")
    @Expose
    private String shortPercentFloat;
    @SerializedName("PercentInsiders")
    @Expose
    private String percentInsiders;
    @SerializedName("PercentInstitutions")
    @Expose
    private String percentInstitutions;
    @SerializedName("ForwardAnnualDividendRate")
    @Expose
    private String forwardAnnualDividendRate;
    @SerializedName("ForwardAnnualDividendYield")
    @Expose
    private String forwardAnnualDividendYield;
    @SerializedName("PayoutRatio")
    @Expose
    private String payoutRatio;
    @SerializedName("DividendDate")
    @Expose
    private String dividendDate;
    @SerializedName("ExDividendDate")
    @Expose
    private String exDividendDate;
    @SerializedName("LastSplitFactor")
    @Expose
    private String lastSplitFactor;
    @SerializedName("LastSplitDate")
    @Expose
    private String lastSplitDate;
    private final static long serialVersionUID = -2308236376125645232L;

    /**
     * No args constructor for use in serialization
     *
     */
    public CompanyOverview() {
    }

    /**
     *
     * @param _50DayMovingAverage
     * @param symbol
     * @param country
     * @param dividendDate
     * @param dividendPerShare
     * @param operatingMarginTTM
     * @param _200DayMovingAverage
     * @param dividendYield
     * @param forwardAnnualDividendRate
     * @param percentInsiders
     * @param revenueTTM
     * @param quarterlyEarningsGrowthYOY
     * @param percentInstitutions
     * @param sector
     * @param pERatio
     * @param bookValue
     * @param marketCapitalization
     * @param latestQuarter
     * @param dilutedEPSTTM
     * @param eps
     * @param sharesFloat
     * @param assetType
     * @param shortPercentFloat
     * @param name
     * @param _52WeekHigh
     * @param exchange
     * @param sharesShortPriorMonth
     * @param payoutRatio
     * @param forwardAnnualDividendYield
     * @param eVToEBITDA
     * @param cik
     * @param analystTargetPrice
     * @param description
     * @param industry
     * @param returnOnAssetsTTM
     * @param fiscalYearEnd
     * @param lastSplitFactor
     * @param eVToRevenue
     * @param trailingPE
     * @param returnOnEquityTTM
     * @param profitMargin
     * @param currency
     * @param priceToBookRatio
     * @param _52WeekLow
     * @param sharesOutstanding
     * @param beta
     * @param address
     * @param exDividendDate
     * @param ebitda
     * @param lastSplitDate
     * @param pEGRatio
     * @param revenuePerShareTTM
     * @param forwardPE
     * @param sharesShort
     * @param quarterlyRevenueGrowthYOY
     * @param shortRatio
     * @param grossProfitTTM
     * @param priceToSalesRatioTTM
     * @param shortPercentOutstanding
     */
    public CompanyOverview(String symbol, String assetType, String name, String description, String cik, String exchange, String currency, String country, String sector, String industry, String address, String fiscalYearEnd, String latestQuarter, String marketCapitalization, String ebitda, String pERatio, String pEGRatio, String bookValue, String dividendPerShare, String dividendYield, String eps, String revenuePerShareTTM, String profitMargin, String operatingMarginTTM, String returnOnAssetsTTM, String returnOnEquityTTM, String revenueTTM, String grossProfitTTM, String dilutedEPSTTM, String quarterlyEarningsGrowthYOY, String quarterlyRevenueGrowthYOY, String analystTargetPrice, String trailingPE, String forwardPE, String priceToSalesRatioTTM, String priceToBookRatio, String eVToRevenue, String eVToEBITDA, String beta, String _52WeekHigh, String _52WeekLow, String _50DayMovingAverage, String _200DayMovingAverage, String sharesOutstanding, String sharesFloat, String sharesShort, String sharesShortPriorMonth, String shortRatio, String shortPercentOutstanding, String shortPercentFloat, String percentInsiders, String percentInstitutions, String forwardAnnualDividendRate, String forwardAnnualDividendYield, String payoutRatio, String dividendDate, String exDividendDate, String lastSplitFactor, String lastSplitDate) {
        super();
        this.symbol = symbol;
        this.assetType = assetType;
        this.name = name;
        this.description = description;
        this.cik = cik;
        this.exchange = exchange;
        this.currency = currency;
        this.country = country;
        this.sector = sector;
        this.industry = industry;
        this.address = address;
        this.fiscalYearEnd = fiscalYearEnd;
        this.latestQuarter = latestQuarter;
        this.marketCapitalization = marketCapitalization;
        this.ebitda = ebitda;
        this.pERatio = pERatio;
        this.pEGRatio = pEGRatio;
        this.bookValue = bookValue;
        this.dividendPerShare = dividendPerShare;
        this.dividendYield = dividendYield;
        this.eps = eps;
        this.revenuePerShareTTM = revenuePerShareTTM;
        this.profitMargin = profitMargin;
        this.operatingMarginTTM = operatingMarginTTM;
        this.returnOnAssetsTTM = returnOnAssetsTTM;
        this.returnOnEquityTTM = returnOnEquityTTM;
        this.revenueTTM = revenueTTM;
        this.grossProfitTTM = grossProfitTTM;
        this.dilutedEPSTTM = dilutedEPSTTM;
        this.quarterlyEarningsGrowthYOY = quarterlyEarningsGrowthYOY;
        this.quarterlyRevenueGrowthYOY = quarterlyRevenueGrowthYOY;
        this.analystTargetPrice = analystTargetPrice;
        this.trailingPE = trailingPE;
        this.forwardPE = forwardPE;
        this.priceToSalesRatioTTM = priceToSalesRatioTTM;
        this.priceToBookRatio = priceToBookRatio;
        this.eVToRevenue = eVToRevenue;
        this.eVToEBITDA = eVToEBITDA;
        this.beta = beta;
        this._52WeekHigh = _52WeekHigh;
        this._52WeekLow = _52WeekLow;
        this._50DayMovingAverage = _50DayMovingAverage;
        this._200DayMovingAverage = _200DayMovingAverage;
        this.sharesOutstanding = sharesOutstanding;
        this.sharesFloat = sharesFloat;
        this.sharesShort = sharesShort;
        this.sharesShortPriorMonth = sharesShortPriorMonth;
        this.shortRatio = shortRatio;
        this.shortPercentOutstanding = shortPercentOutstanding;
        this.shortPercentFloat = shortPercentFloat;
        this.percentInsiders = percentInsiders;
        this.percentInstitutions = percentInstitutions;
        this.forwardAnnualDividendRate = forwardAnnualDividendRate;
        this.forwardAnnualDividendYield = forwardAnnualDividendYield;
        this.payoutRatio = payoutRatio;
        this.dividendDate = dividendDate;
        this.exDividendDate = exDividendDate;
        this.lastSplitFactor = lastSplitFactor;
        this.lastSplitDate = lastSplitDate;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getAssetType() {
        return assetType;
    }

    public void setAssetType(String assetType) {
        this.assetType = assetType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCik() {
        return cik;
    }

    public void setCik(String cik) {
        this.cik = cik;
    }

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getSector() {
        return sector;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }

    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getFiscalYearEnd() {
        return fiscalYearEnd;
    }

    public void setFiscalYearEnd(String fiscalYearEnd) {
        this.fiscalYearEnd = fiscalYearEnd;
    }

    public String getLatestQuarter() {
        return latestQuarter;
    }

    public void setLatestQuarter(String latestQuarter) {
        this.latestQuarter = latestQuarter;
    }

    public String getMarketCapitalization() {
        return marketCapitalization;
    }

    public void setMarketCapitalization(String marketCapitalization) {
        this.marketCapitalization = marketCapitalization;
    }

    public String getEbitda() {
        return ebitda;
    }

    public void setEbitda(String ebitda) {
        this.ebitda = ebitda;
    }

    public String getPERatio() {
        return pERatio;
    }

    public void setPERatio(String pERatio) {
        this.pERatio = pERatio;
    }

    public String getPEGRatio() {
        return pEGRatio;
    }

    public void setPEGRatio(String pEGRatio) {
        this.pEGRatio = pEGRatio;
    }

    public String getBookValue() {
        return bookValue;
    }

    public void setBookValue(String bookValue) {
        this.bookValue = bookValue;
    }

    public String getDividendPerShare() {
        return dividendPerShare;
    }

    public void setDividendPerShare(String dividendPerShare) {
        this.dividendPerShare = dividendPerShare;
    }

    public String getDividendYield() {
        return dividendYield;
    }

    public void setDividendYield(String dividendYield) {
        this.dividendYield = dividendYield;
    }

    public String getEps() {
        return eps;
    }

    public void setEps(String eps) {
        this.eps = eps;
    }

    public String getRevenuePerShareTTM() {
        return revenuePerShareTTM;
    }

    public void setRevenuePerShareTTM(String revenuePerShareTTM) {
        this.revenuePerShareTTM = revenuePerShareTTM;
    }

    public String getProfitMargin() {
        return profitMargin;
    }

    public void setProfitMargin(String profitMargin) {
        this.profitMargin = profitMargin;
    }

    public String getOperatingMarginTTM() {
        return operatingMarginTTM;
    }

    public void setOperatingMarginTTM(String operatingMarginTTM) {
        this.operatingMarginTTM = operatingMarginTTM;
    }

    public String getReturnOnAssetsTTM() {
        return returnOnAssetsTTM;
    }

    public void setReturnOnAssetsTTM(String returnOnAssetsTTM) {
        this.returnOnAssetsTTM = returnOnAssetsTTM;
    }

    public String getReturnOnEquityTTM() {
        return returnOnEquityTTM;
    }

    public void setReturnOnEquityTTM(String returnOnEquityTTM) {
        this.returnOnEquityTTM = returnOnEquityTTM;
    }

    public String getRevenueTTM() {
        return revenueTTM;
    }

    public void setRevenueTTM(String revenueTTM) {
        this.revenueTTM = revenueTTM;
    }

    public String getGrossProfitTTM() {
        return grossProfitTTM;
    }

    public void setGrossProfitTTM(String grossProfitTTM) {
        this.grossProfitTTM = grossProfitTTM;
    }

    public String getDilutedEPSTTM() {
        return dilutedEPSTTM;
    }

    public void setDilutedEPSTTM(String dilutedEPSTTM) {
        this.dilutedEPSTTM = dilutedEPSTTM;
    }

    public String getQuarterlyEarningsGrowthYOY() {
        return quarterlyEarningsGrowthYOY;
    }

    public void setQuarterlyEarningsGrowthYOY(String quarterlyEarningsGrowthYOY) {
        this.quarterlyEarningsGrowthYOY = quarterlyEarningsGrowthYOY;
    }

    public String getQuarterlyRevenueGrowthYOY() {
        return quarterlyRevenueGrowthYOY;
    }

    public void setQuarterlyRevenueGrowthYOY(String quarterlyRevenueGrowthYOY) {
        this.quarterlyRevenueGrowthYOY = quarterlyRevenueGrowthYOY;
    }

    public String getAnalystTargetPrice() {
        return analystTargetPrice;
    }

    public void setAnalystTargetPrice(String analystTargetPrice) {
        this.analystTargetPrice = analystTargetPrice;
    }

    public String getTrailingPE() {
        return trailingPE;
    }

    public void setTrailingPE(String trailingPE) {
        this.trailingPE = trailingPE;
    }

    public String getForwardPE() {
        return forwardPE;
    }

    public void setForwardPE(String forwardPE) {
        this.forwardPE = forwardPE;
    }

    public String getPriceToSalesRatioTTM() {
        return priceToSalesRatioTTM;
    }

    public void setPriceToSalesRatioTTM(String priceToSalesRatioTTM) {
        this.priceToSalesRatioTTM = priceToSalesRatioTTM;
    }

    public String getPriceToBookRatio() {
        return priceToBookRatio;
    }

    public void setPriceToBookRatio(String priceToBookRatio) {
        this.priceToBookRatio = priceToBookRatio;
    }

    public String getEVToRevenue() {
        return eVToRevenue;
    }

    public void setEVToRevenue(String eVToRevenue) {
        this.eVToRevenue = eVToRevenue;
    }

    public String getEVToEBITDA() {
        return eVToEBITDA;
    }

    public void setEVToEBITDA(String eVToEBITDA) {
        this.eVToEBITDA = eVToEBITDA;
    }

    public String getBeta() {
        return beta;
    }

    public void setBeta(String beta) {
        this.beta = beta;
    }

    public String get52WeekHigh() {
        return _52WeekHigh;
    }

    public void set52WeekHigh(String _52WeekHigh) {
        this._52WeekHigh = _52WeekHigh;
    }

    public String get52WeekLow() {
        return _52WeekLow;
    }

    public void set52WeekLow(String _52WeekLow) {
        this._52WeekLow = _52WeekLow;
    }

    public String get50DayMovingAverage() {
        return _50DayMovingAverage;
    }

    public void set50DayMovingAverage(String _50DayMovingAverage) {
        this._50DayMovingAverage = _50DayMovingAverage;
    }

    public String get200DayMovingAverage() {
        return _200DayMovingAverage;
    }

    public void set200DayMovingAverage(String _200DayMovingAverage) {
        this._200DayMovingAverage = _200DayMovingAverage;
    }

    public String getSharesOutstanding() {
        return sharesOutstanding;
    }

    public void setSharesOutstanding(String sharesOutstanding) {
        this.sharesOutstanding = sharesOutstanding;
    }

    public String getSharesFloat() {
        return sharesFloat;
    }

    public void setSharesFloat(String sharesFloat) {
        this.sharesFloat = sharesFloat;
    }

    public String getSharesShort() {
        return sharesShort;
    }

    public void setSharesShort(String sharesShort) {
        this.sharesShort = sharesShort;
    }

    public String getSharesShortPriorMonth() {
        return sharesShortPriorMonth;
    }

    public void setSharesShortPriorMonth(String sharesShortPriorMonth) {
        this.sharesShortPriorMonth = sharesShortPriorMonth;
    }

    public String getShortRatio() {
        return shortRatio;
    }

    public void setShortRatio(String shortRatio) {
        this.shortRatio = shortRatio;
    }

    public String getShortPercentOutstanding() {
        return shortPercentOutstanding;
    }

    public void setShortPercentOutstanding(String shortPercentOutstanding) {
        this.shortPercentOutstanding = shortPercentOutstanding;
    }

    public String getShortPercentFloat() {
        return shortPercentFloat;
    }

    public void setShortPercentFloat(String shortPercentFloat) {
        this.shortPercentFloat = shortPercentFloat;
    }

    public String getPercentInsiders() {
        return percentInsiders;
    }

    public void setPercentInsiders(String percentInsiders) {
        this.percentInsiders = percentInsiders;
    }

    public String getPercentInstitutions() {
        return percentInstitutions;
    }

    public void setPercentInstitutions(String percentInstitutions) {
        this.percentInstitutions = percentInstitutions;
    }

    public String getForwardAnnualDividendRate() {
        return forwardAnnualDividendRate;
    }

    public void setForwardAnnualDividendRate(String forwardAnnualDividendRate) {
        this.forwardAnnualDividendRate = forwardAnnualDividendRate;
    }

    public String getForwardAnnualDividendYield() {
        return forwardAnnualDividendYield;
    }

    public void setForwardAnnualDividendYield(String forwardAnnualDividendYield) {
        this.forwardAnnualDividendYield = forwardAnnualDividendYield;
    }

    public String getPayoutRatio() {
        return payoutRatio;
    }

    public void setPayoutRatio(String payoutRatio) {
        this.payoutRatio = payoutRatio;
    }

    public String getDividendDate() {
        return dividendDate;
    }

    public void setDividendDate(String dividendDate) {
        this.dividendDate = dividendDate;
    }

    public String getExDividendDate() {
        return exDividendDate;
    }

    public void setExDividendDate(String exDividendDate) {
        this.exDividendDate = exDividendDate;
    }

    public String getLastSplitFactor() {
        return lastSplitFactor;
    }

    public void setLastSplitFactor(String lastSplitFactor) {
        this.lastSplitFactor = lastSplitFactor;
    }

    public String getLastSplitDate() {
        return lastSplitDate;
    }

    public void setLastSplitDate(String lastSplitDate) {
        this.lastSplitDate = lastSplitDate;
    }

}
