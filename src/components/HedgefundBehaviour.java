package components;

/**
 * Created by eva on 24/01/2017.
 */
public class HedgefundBehaviour {
    Hedgefund hedgefund;
    Market market;
    double alpha;


    public HedgefundBehaviour(){
        this.hedgefund=null;
        this.alpha=0.001;
    }

    public void printBank(){
        System.out.println(this.hedgefund.name);
    }

    public void updateBalanceSheet(){
        this.hedgefund.Balancesheet.phi++;
    }

    public void setMarket(Market market){
        this.market=market;
    }

    public void getFundingUpdate(int i, double newfunding){
        if(i==1){this.hedgefund.Omega_1_=newfunding;}
        else  if(i==2){this.hedgefund.Omega_2_=newfunding;}
        else if(i==3){this.hedgefund.Omega_3_=newfunding;}
        else if(i==4){this.hedgefund.Omega_4_=newfunding;}
        else if(i==5){this.hedgefund.Omega_5_=newfunding;}
        else{System.out.println("there is no bank w this number"+i);}

    }



    public void checkSolvency(){
        if(this.hedgefund.getBalancesheet().phi*market.S>=this.hedgefund.getTotalNewFunding()){
            this.hedgefund.D=1;}
            else{this.hedgefund.D=0;}


    }

    public void repayFunding(){
        double deltaFunding=this.hedgefund.getBalancesheet().getTotalFunding()-this.hedgefund.getTotalNewFunding();
        this.hedgefund.z=Math.min(this.hedgefund.getBalancesheet().getCash(), this.hedgefund.D*deltaFunding);
        this.hedgefund.y=Math.min(this.hedgefund.D*deltaFunding-this.hedgefund.z, this.hedgefund.Balancesheet.getPhi()*market.S);
    }

    public void marginCall(){
        if(this.hedgefund.Balancesheet.getPhi()*market.S+this.hedgefund.getBalancesheet().getCash()-this.hedgefund.y-this.hedgefund.z>=this.hedgefund.getTotalNewFunding()/(1-alpha)){
            this.hedgefund.D_=1;}
            else{this.hedgefund.D_=0;
        }

        this.hedgefund.MC=this.hedgefund.D*this.hedgefund.D_*Math.max(this.hedgefund.getTotalNewFunding()/(1-alpha)-this.hedgefund.Balancesheet.getPhi()*market.S+this.hedgefund.y,0);
    }

    public double collateralNeeded(){
        return this.hedgefund.getTotalNewFunding()/(1-alpha);
    }
}
