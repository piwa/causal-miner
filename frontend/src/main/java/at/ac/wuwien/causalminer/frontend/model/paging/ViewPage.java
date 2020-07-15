package at.ac.wuwien.causalminer.frontend.model.paging;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ViewPage<T> {

    public ViewPage(List<T> data) {
        this.data = data;
    }

    private List<T> data;
    private long recordsFiltered;
    private long recordsTotal;
    private int draw;

}