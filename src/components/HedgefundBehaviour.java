package components;

/**
 * Created by eva on 24/01/2017.
 */
public class HedgefundBehaviour {

    Hedgefund hedgefund;
    Market market;
    InfoExchange infoExchange;

    public double redbuf;


    //these functions find the total new funding and the amount that is going to have to be paid back. totalnewfunding is here, total funding
    //in balancesheet because total funding is already marked on the BS and new funding is not

    public double getTotalNewFunding(){

        double sum = 0;
        double[] newFundingColumn = infoExchange.newFunding.transpose().getArray()[hedgefund.id];

        for (double repo : newFundingColumn) {
            sum += repo;
        }

        return sum;
    }

    public double findPayBack(){
        return hedgefund.balanceSheet.getTotalFunding()-getTotalNewFunding();
    }

//    public double percentchangefunding(){ if(hedgefund.getBalancesheet().getTotalFunding()>0){return (getTotalNewFunding()-hedgefund.getBalancesheet().getTotalFunding())/hedgefund.getBalancesheet().getTotalFunding();}
//    else{return 0;}}


    public double findRedemptions(){
        if(market.delta<redbuf){
            return -(hedgefund.balanceSheet.calculateEquity()*((Math.exp(market.delta*market.eta)-1)/(Math.exp(1)-1)));}
        else return 0;
    }

//    public double returnLeverage(){
//        double equity = Math.max(hedgefund.balanceSheet.phi*market.S+hedgefund.balanceSheet.C-hedgefund.balanceSheet.getTotalFunding(),0);
//        double lambda = equity/(this.hedgefund.balanceSheet.phi*market.S+this.hedgefund.balanceSheet.C-findPayBack());
//        return lambda;
//    }

    //NOTE THIS IS NOW SET UP TO WORK ONLY BECAUSE WE GIVE THE HEDGEFUND ONLY ONE BANK!!!!!
    public void checkCasesandAct(){
        if((hedgefund.balanceSheet.phi*market.S+hedgefund.balanceSheet.C-findPayBack()-findRedemptions())>=getTotalNewFunding()/(1-market.alpha)){
            hedgefund.z=Math.min(hedgefund.getBalancesheet().getCash(), (findPayBack()+findRedemptions()));
            hedgefund.y= (findPayBack()+findRedemptions())-hedgefund.z;
            hedgefund.D=1;
            hedgefund.EndLoan=1; //means NOT end

        } else if ((hedgefund.balanceSheet.phi*market.S+hedgefund.balanceSheet.C-findRedemptions()-findPayBack())<getTotalNewFunding()/(1-market.alpha)
                && (hedgefund.balanceSheet.phi*market.S+hedgefund.balanceSheet.C-findPayBack()-findRedemptions())>=getTotalNewFunding()){
            hedgefund.z=Math.min(hedgefund.getBalancesheet().getCash(), (hedgefund.balanceSheet.getTotalFunding()+findRedemptions()));
            hedgefund.y= (hedgefund.getBalancesheet().getTotalFunding()+findRedemptions())-hedgefund.z;
            hedgefund.D=1;
            hedgefund.EndLoan=0;
            System.out.println("Hedgefund "+hedgefund.id+" has been forced to pay back all funding, and survived");

            //problem: we're not actually including reduction of funding here
        } else {
            System.out.println("Hedgefund "+hedgefund.id+" is defaulting because it can't pay everything back");

            hedgefund.D=0;
            hedgefund.EndLoan=1; //means NOT ending actively
        }

        infoExchange.hedgefundDefaults[hedgefund.id]=hedgefund.D;
    }


//    public void checkDefault(){
//        if(returnLeverage()>=market.alpha){
//            System.out.println(returnLeverage());
//            hedgefund.D=1;}
//            else{hedgefund.D=0;
//            System.out.println("I am "+hedgefund.name+" and I have just become insolvent");}
//        infoExchange.hedgefundDefaults[hedgefund.id]=hedgefund.D;
//    }
//
//    //it decides how much of the amount to be paid back it will pay back using cash and how much by selling stocks. these amounts are stored in the
//    //temporary variables in the hedgefund object, y and z
//    public void repayFunding(){
//        if(findRedemptions()+findPayBack()>0){System.out.println("I am "+hedgefund.name+" and I am paying "+(findPayBack()+findRedemptions()));}
//        hedgefund.z=Math.min(hedgefund.getBalancesheet().getCash(), hedgefund.D*(findPayBack()+findRedemptions()));
//        hedgefund.y= hedgefund.D*(findPayBack()+findRedemptions())-hedgefund.z;
//    }

//    //now it checks whether it holds enough stock for its collateral needs, if this is not enough it decides how many extra assets it needs
//    //to buy. all this info is stored in variables in the hedgefund object.
//    public void marginCall(){
//        this.hedgefund.MC=Math.max((getTotalNewFunding())/(1-market.alpha)-hedgefund.getBalancesheet().phi*market.S,0);
//        if(hedgefund.MC>0){System.out.println("My name is"+hedgefund.name+"I am spending "+hedgefund.MC+" on assets to meet my Margin Call");}
//    }

    //now the hedgefund lets the bank know whether it has defaulted, and if so, how much it's going to be able to repay. all this information is stored
    //in one entry in the "repayments" matrix; the value shows how much the hedgefund is going to be able to give back to the bank
    public void giveUpdate() {
        int N_BANKS = infoExchange.repos.getRowDimension();
        for (int j = 0; j < N_BANKS; j++) {
            if (hedgefund.getBalancesheet().getTotalFunding() > 0)
            {
                infoExchange.repayments.set(j, hedgefund.id, (infoExchange.repos.get(j, hedgefund.id) / hedgefund.getBalancesheet().getTotalFunding()) * Math.min(hedgefund.getBalancesheet().phi * market.S + hedgefund.getBalancesheet().C, hedgefund.getBalancesheet().getTotalFunding()) * (1 - hedgefund.D));

                infoExchange.loanTerminationsHF.set(j, hedgefund.id,hedgefund.EndLoan);
            }
            else{infoExchange.repayments.set(j, hedgefund.id, 0);}
        }
    }


   //finally it collects its orders and updates the balancesheet with all the temporary information stored in the hedgefund object variables.
    public double placeMarketOrder(){
        //NEEDS TO HAPPEN BEFORE UPDATE BALANCESHEET
        double Order=this.hedgefund.y*this.hedgefund.D/market.S+this.hedgefund.getBalancesheet().phi*(1-this.hedgefund.D);
        return Order;
    }

 public void updateBalanceSheet(){
     int N_BANKS=infoExchange.repos.getRowDimension();


     hedgefund.getBalancesheet().phi=(hedgefund.getBalancesheet().phi-hedgefund.y/hedgefund.Behaviour.market.S)*hedgefund.D;
     hedgefund.getBalancesheet().C=(hedgefund.getBalancesheet().C-hedgefund.z)*hedgefund.D;
        int j;
        for(j=0; j<N_BANKS;j++){
            infoExchange.repos.set(j, hedgefund.id, infoExchange.newFunding.get(j,hedgefund.id)*hedgefund.D*hedgefund.EndLoan);}

     if(this.hedgefund.D==0){
         this.hedgefund.DEFAULTED=1;}
     hedgefund.D=1;

  }

    //these are administrative

    public HedgefundBehaviour(){
        this.hedgefund=null;
    }

    public void printBank(){
        System.out.println(this.hedgefund.name);
    }

    public void updateBalanceSheetOld(){
        this.hedgefund.getBalancesheet().phi++;
    }

    public void setMarket(Market market){
        this.market=market;
    }

    public void setRedbuf(double redbuf){this.redbuf=redbuf;}

    public void setInfoExchange(InfoExchange infoExchange){
        this.infoExchange=infoExchange;
    }


}
