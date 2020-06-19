package amberdb.sql;
import javax.persistence.Column;
import javax.persistence.Entity;


@Entity
public  class CarrierAlgorithm {
    @Column
    Long linkId;
    @Column
    String name;
    @Column
    Long carrierId;
    @Column 
    Long algorithmId;
    
    public CarrierAlgorithm(String name, Long carrierId, Long algorithmId){
        this.name = name;
        this.carrierId = carrierId;
        this.algorithmId = algorithmId;
                  
    }
    
    public CarrierAlgorithm(Long linkId, String name, Long carrierId, Long string){
        this.linkId = linkId;
        this.name = name;
        this.carrierId = carrierId;
        this.algorithmId = string;
                  
    }

 

    public Long getLinkId() {
        return linkId;
    }



    public void setLinkId(Long linkId) {
        this.linkId = linkId;
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

  
    
    public Long getCarrierId() {
        return carrierId;
    }



    public void setCarrierId(Long carrierId) {
        this.carrierId = carrierId;
    }



    public Long getAlgorithmId() {
        return algorithmId;
    }



    public void setAlgorithmId(Long algorithmId) {
        this.carrierId = algorithmId;
    }


    

}
