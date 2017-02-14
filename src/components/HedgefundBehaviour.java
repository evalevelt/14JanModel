package components;

/**
 * Created by eva on 24/01/2017.
 */
public class HedgefundBehaviour {
    Hedgefund hedgefund;
    Market market;
    InfoExchange infoExchange;
    public double alpha;


    //first the hedgefund stores the information the bank passes on about changes in funding, it does this in the Hedgefund object
//    public void getFundingUpdate(int i, double newfunding){
//        if(i==1){this.hedgefund.Omega_1_=newfunding;}
//        else  if(i==2){this.hedgefund.Omega_2_=newfunding;}
//        else if(i==3){this.hedgefund.Omega_3_=newfunding;}
//        else if(i==4){this.hedgefund.Omega_4_=newfunding;}
//        else if(i==5){this.hedgefund.Omega_5_=newfunding;}
//        else{System.out.println("there is no bank w this number"+i);}
//
//    }


    //now it checks whether it is still solvent with the changed funding and changed market price, if it's newly insolvent this step this
    // is stored in D in the hedgefund object
    public void checkSolvency(){
        if(this.hedgefund.getBalancesheet().phi*market.S+hedgefund.getBalancesheet().C>=this.hedgefund.getTotalNewFunding()){
            this.hedgefund.D=1;}
            else{this.hedgefund.D=0;
            System.out.println("I am "+hedgefund.name+" and I have just become insolvent");}


    }

    //now it uses the information on how much funding it has now and how much it had before to figure out what it needs to pay back.
    //it decides how much of this it will pay back using cash and how much by selling stocks. these amounts are stored in the
    //temporary variables in the hedgefund object, y and z
    public void repayFunding(){
        double deltaFunding=this.hedgefund.getBalancesheet().getTotalFunding()-this.hedgefund.getTotalNewFunding();
        if(deltaFunding>0){System.out.println("I am "+hedgefund.name+" and I am repaying "+deltaFunding);}
        this.hedgefund.z=Math.min(this.hedgefund.getBalancesheet().getCash(), this.hedgefund.D*deltaFunding);
        this.hedgefund.y=Math.min(this.hedgefund.D*deltaFunding-this.hedgefund.z, this.hedgefund.getBalancesheet().getPhi()*market.S);
    }

    //now it checks whether it holds enough stock for its collateral needs, if this is not enough it decides how many extra assets it needs
    //to buy. if it does not have enough cash to buy assets for its collateral needs, it defaults. all this info is stored in variables in
    //the hedgefund object.
    public void marginCall(){
        if(this.hedgefund.getBalancesheet().getPhi()*market.S+this.hedgefund.getBalancesheet().getCash()-this.hedgefund.y-this.hedgefund.z>=this.hedgefund.getTotalNewFunding()/(1-alpha)){
            this.hedgefund.D_=1;}
            else if (this.hedgefund.D==0){
            this.hedgefund.D_=1;
        }

            else{this.hedgefund.D_=0;
            System.out.println("I am hedgefund "+hedgefund.name+"and I am defaulting due to Margin Call");
        }

        this.hedgefund.MC=this.hedgefund.D*this.hedgefund.D_*Math.max(this.hedgefund.getTotalNewFunding()/(1-alpha)-this.hedgefund.getBalancesheet().getPhi()*market.S+this.hedgefund.y,0);
        if(hedgefund.MC>0){System.out.println("I am spending "+hedgefund.MC+" on assets to meet my Margin Call");}
    }

    public double collateralNeeded(){
        return this.hedgefund.getTotalNewFunding()/(1-alpha);
    }

   //finally it collects its orders and updates the balancesheet with all the temporary information stored in the hedgefund object variables.
    public double placeMarketOrder(){
        //NEEDS TO HAPPEN BEFORE UPDATE BALANCESHEET
        double Order=(this.hedgefund.MC-this.hedgefund.y)*this.hedgefund.D*this.hedgefund.D_-this.hedgefund.getBalancesheet().phi*market.S*(1-this.hedgefund.D)*(1-this.hedgefund.D_);
        return Order;
    }

 public void updateBalanceSheet(int N_BANKS){
       hedgefund.getBalancesheet().phi=(hedgefund.getBalancesheet().phi+(hedgefund.MC-hedgefund.y)/hedgefund.Behaviour.market.S)*hedgefund.D*hedgefund.D_;
        hedgefund.getBalancesheet().C=(hedgefund.getBalancesheet().C-hedgefund.z-hedgefund.MC)*hedgefund.D*hedgefund.D_;
        int j;
        for(j=0; j<N_BANKS;j++){
            hedgefund.repos.set(j, hedgefund.id, hedgefund.newFunding.get(j,hedgefund.id)*hedgefund.D*hedgefund.D_);
        }

  }

  public void giveDefaults(){
     hedgefund.hedgefundDefaults[hedgefund.id]=hedgefund.D*hedgefund.D_;
  }

    //these are administrative

    public HedgefundBehaviour(){
        this.hedgefund=null;
        this.alpha=0.001;
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
