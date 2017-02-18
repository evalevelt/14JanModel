package components;

/**
 * Created by eva on 24/01/2017.
 */
public class HedgefundBehaviour {
    Hedgefund hedgefund;
    Market market;
    InfoExchange infoExchange;
    public double alpha;


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
        return undefaultedDebt()-getTotalNewFunding();
    }

    public double undefaultedDebt(){
        int N_BANKS=infoExchange.repos.getRowDimension();

        double sum=0;
        int j;
        for(j=0; j<N_BANKS; j++){
            sum=infoExchange.repos.get(j, hedgefund.id)*infoExchange.bankDefaults1[j]+sum;
        }
        return sum;
    }

    public double returnLeverage(){
        double equity = hedgefund.balanceSheet.phi*market.S+hedgefund.balanceSheet.C- undefaultedDebt();
        double lambda = equity/(this.hedgefund.balanceSheet.phi*market.S+this.hedgefund.balanceSheet.C-findPayBack());
        return lambda;
    }


    public void checkDefault(){
        System.out.println("I am "+hedgefund.name+"and im checking for default. I've found leverage"+returnLeverage());
        if(returnLeverage()>=alpha){
            hedgefund.D=1;}
            else{hedgefund.D=0;
            System.out.println("I am "+hedgefund.name+" and I have just become insolvent");}
        infoExchange.hedgefundDefaults[hedgefund.id]=hedgefund.D;


    }

    //now it uses the information on how much funding it has now and how much it had before to figure out what it needs to pay back.
    //it decides how much of this it will pay back using cash and how much by selling stocks. these amounts are stored in the
    //temporary variables in the hedgefund object, y and z
    public void repayFunding(){
        if(findPayBack()>0){System.out.println("I am "+hedgefund.name+" and I am repaying "+findPayBack());}
        hedgefund.z=Math.min(hedgefund.getBalancesheet().getCash(), hedgefund.D*findPayBack());
       hedgefund.y= hedgefund.D*findPayBack()-hedgefund.z;
    }

    //now it checks whether it holds enough stock for its collateral needs, if this is not enough it decides how many extra assets it needs
    //to buy. if it does not have enough cash to buy assets for its collateral needs, it defaults. all this info is stored in variables in
    //the hedgefund object.
    public void marginCall(){
        this.hedgefund.MC=this.hedgefund.D*Math.max((undefaultedDebt()-findPayBack())/(1-alpha)-hedgefund.getBalancesheet().phi*market.S,0);
        if(hedgefund.MC>0){System.out.println("I am spending "+hedgefund.MC+" on assets to meet my Margin Call");}
    }

    public void giveUpdate(){
        int N_BANKS=infoExchange.repos.getRowDimension();

        int j;
        for(j=0; j<N_BANKS; j++){
            infoExchange.repayments.set(j, hedgefund.id, (infoExchange.repos.get(j, hedgefund.id)/hedgefund.getBalancesheet().getTotalFunding())*(hedgefund.getBalancesheet().phi*market.S-hedgefund.getBalancesheet().C)*(1-hedgefund.D));
        }

    }


   //finally it collects its orders and updates the balancesheet with all the temporary information stored in the hedgefund object variables.
    public double placeMarketOrder(){
        //NEEDS TO HAPPEN BEFORE UPDATE BALANCESHEET
        double Order=(this.hedgefund.MC-this.hedgefund.y)*this.hedgefund.D-this.hedgefund.getBalancesheet().phi*market.S*(1-this.hedgefund.D);
        return Order;
    }

 public void updateBalanceSheet(){
     int N_BANKS=infoExchange.repos.getRowDimension();


     hedgefund.getBalancesheet().phi=(hedgefund.getBalancesheet().phi+(hedgefund.MC-hedgefund.y)/hedgefund.Behaviour.market.S)*hedgefund.D;
        hedgefund.getBalancesheet().C=(hedgefund.getBalancesheet().C-hedgefund.z-hedgefund.MC)*hedgefund.D;
        int j;
        for(j=0; j<N_BANKS;j++){
            infoExchange.repos.set(j, hedgefund.id, infoExchange.newFunding.get(j,hedgefund.id)*infoExchange.bankDefaults2[j]*hedgefund.D);
        }

  }

//  public void giveDefaults(){
//     hedgefund.hedgefundDefaults[hedgefund.id]=hedgefund.D*hedgefund.D_;
//  }

    //these are administrative

    public HedgefundBehaviour(){
        this.hedgefund=null;
        this.alpha=0.25;
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

    public void setInfoExchange(InfoExchange infoExchange){
        this.infoExchange=infoExchange;
    }


}
