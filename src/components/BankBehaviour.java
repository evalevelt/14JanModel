package components;

/**
 * Created by eva on 24/01/2017.
 */
public class BankBehaviour {
    Bank bank;
    Market market;
    InfoExchange infoExchange;
   public double lambda_min = 0.03;
   public double lambda_T=0.04;
   public double kappa_min=0.9;
    public double kappa_T=1.1;
    double beta=0.6;



//these are all actions that are taken in a timestep


    //here the bank checks if its just become insolvent, it updates D in the bank object to reflect whether that has happened this timestep
    public void checkSolvency(){
        if(this.bank.balanceSheet.phi*market.S+this.bank.balanceSheet.getCash()>=this.bank.balanceSheet.getLiability()){
            this.bank.D=1;
        } else {this.bank.D=0;
        System.out.println("I am "+bank.name+" and I have just become insolvent");}
    }

    //these two functions are to find the leverage and discover if its below the minimum. it updates B in the bank object to reflect
    //whether the bank is delevering THIS timestep
    public double returnLeverage(){
        double lambda = (this.bank.balanceSheet.phi*market.S+this.bank.balanceSheet.getCash()-this.bank.balanceSheet.getLiability())/(this.bank.balanceSheet.phi*market.S+this.bank.balanceSheet.getTotalRepo()+this.bank.balanceSheet.getCash());
        return lambda;
    }

    public void checkLeverage(){
        double lambda = (this.bank.balanceSheet.phi*market.S+this.bank.balanceSheet.getCash()-this.bank.balanceSheet.getLiability())/(this.bank.balanceSheet.phi*market.S+this.bank.balanceSheet.getTotalRepo()+this.bank.balanceSheet.getCash());
        if(this.bank.balanceSheet.phi*market.S+this.bank.balanceSheet.getTotalRepo()>0
                && this.bank.D==1 && lambda<lambda_min){
            this.bank.B=1;
            System.out.println("I am "+bank.name+" and I am delevering");
        } else {this.bank.B=0;}
    }

    //now by either Rule1 or Rule2 the bank decides how to delever (and how much), this is stored in variables x,y,z in the bank object
    //reflecting reduction of cash, stocks and repo

    public double amountToDelever(){
        double Gamma=(lambda_T*(this.bank.balanceSheet.getTotalRepo())+this.bank.balanceSheet.getLiability()+(lambda_T-1)*(this.bank.balanceSheet.phi*market.S+this.bank.balanceSheet.getCash()))*this.bank.B/lambda_T;
        return Gamma;
    }

    public void deleverRule1(){
        double Gamma = amountToDelever();
        this.bank.x=Math.min(Gamma, this.bank.balanceSheet.getTotalRepo());
        if(bank.x>0){System.out.println("I am" +bank.name+" and I am reducing repo funding");}
        if(this.bank.balanceSheet.phi*market.S+this.bank.balanceSheet.getCash()>0){
        this.bank.y=((this.bank.balanceSheet.phi*market.S)/(this.bank.balanceSheet.phi*market.S+this.bank.balanceSheet.getCash()))*(Gamma-this.bank.x);
            this.bank.z=((this.bank.balanceSheet.getCash())/(this.bank.balanceSheet.phi*market.S+this.bank.balanceSheet.getCash()))*(Gamma-this.bank.x);
        }
        if(bank.y>0){System.out.println("I am" +bank.name+" and I am selling assets to delever");}
        else {this.bank.y=0;
        this.bank.z=0;}

    }

    public void deleverRule2(){
        double Gamma = amountToDelever();
        this.bank.x=Math.min(Gamma, this.bank.balanceSheet.getTotalRepo());
        if(bank.x>0){System.out.println("I am " +bank.name+" and I am reducing repo funding");}
        this.bank.y=Math.min(Gamma-this.bank.x, this.bank.balanceSheet.phi*market.S);
        if(bank.y>0){System.out.println("I am " +bank.name+" and I am selling assets to delever");}
        double Gamma_=(lambda_min*(this.bank.balanceSheet.getTotalRepo())+this.bank.balanceSheet.getLiability()+(lambda_min-1)*(this.bank.balanceSheet.phi*market.S+this.bank.balanceSheet.getCash()))*this.bank.B/lambda_min;
        this.bank.z=Math.max(Math.min(Gamma_-this.bank.x-this.bank.y, this.bank.balanceSheet.getCash()),0);
    }

    //should you be using the LCR, this function checks if delevering hasnt brought you to breach LCR.
    // it updates D_ in the bank object to reflect whether that has happened this timest
    public void checkLCR(){
       if(this.bank.balanceSheet.getCash()-this.bank.z>=kappa_min*this.bank.NO){
           this.bank.D_=1;}
           else{this.bank.D_=0;
           System.out.println("I am "+bank.name+" and I have just defaulted by breaching LCR");}

       }


    //these functions have the purpose of communicating with hedgefunds.
    public void giveFundingUpdate(int i){
        double factor =0;
        if(this.bank.getBalanceSheet().getTotalRepo()>0){
                factor=1-(this.bank.x/this.bank.getBalanceSheet().getTotalRepo());}
                else{factor=0;}

         bank.newFunding.set(bank.id,i,bank.D*this.bank.D_*bank.repos.get(bank.id,i)*factor);

    }




    //these functions use the information stored in the temporary variables in the bank object to place market order and update
    //the actual balancesheet at the end of the timestep.
    public double placeMarketOrder(){
        //NEEDS TO HAPPEN BEFORE UPDATE BALANCESHEET
        double Order=-this.bank.y*this.bank.D*this.bank.D_-this.bank.balanceSheet.phi*market.S*(1-this.bank.D)*(1-this.bank.D_);
        return Order;
    }

    public void updateBalancesheet(int N_HEDGEFUNDS){
        double totRepo=giveOldTotalRepo();
        double factor =0;
        if(totRepo>0){
            factor=1-(bank.x/totRepo);}
        else{factor=0;}

        this.bank.balanceSheet.phi=(this.bank.balanceSheet.phi-this.bank.y/market.S)*this.bank.D_*this.bank.D;
        this.bank.balanceSheet.C=(this.bank.balanceSheet.C-this.bank.z)*this.bank.D*this.bank.D_;

        int i;
        for(i=0; i<N_HEDGEFUNDS;i++){
            bank.repos.set(bank.id, i, bank.hedgefundDefaults[i]*bank.D*bank.D_*bank.repos.get(bank.id,i)*factor);
        }
        this.bank.balanceSheet.L=(this.bank.balanceSheet.L-bank.y-bank.z)*bank.D*bank.D_;


    }

    public double giveOldTotalRepo(){return this.bank.getBalanceSheet().getTotalRepo();}


    //these are "administrative" methods

    public BankBehaviour(){
        this.bank=null;
    }

    public void printBank(){
        System.out.println(this.bank.name);
    }

    public void setMarket(Market market){
        this.market=market;
    }

    public void setInfoExchange(InfoExchange infoExchange){
        this.infoExchange=infoExchange;
    }


    public void setNO(){this.bank.NO=this.beta*this.bank.balanceSheet.getLiability();}

    public double getNO(){return this.bank.NO;}


}
