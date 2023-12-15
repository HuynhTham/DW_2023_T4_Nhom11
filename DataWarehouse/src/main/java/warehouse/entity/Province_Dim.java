package warehouse.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Province_Dim {

        private int id;
        private String name;
        private String region;

        public Province_Dim(String name, String region) {
                this.name = name;
                this.region = region;
        }
        public Province_Dim(){}
}
