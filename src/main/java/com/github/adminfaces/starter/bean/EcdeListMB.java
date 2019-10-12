package com.github.adminfaces.starter.bean;

import com.github.adminfaces.starter.infra.model.Filter;
import com.github.adminfaces.starter.model.Ecde;
import com.github.adminfaces.starter.service.EcdeService;
import com.github.adminfaces.template.exception.BusinessException;
import org.omnifaces.cdi.ViewScoped;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

import static com.github.adminfaces.starter.util.Utils.addDetailMessage;

/**
 * Created by rmpestano on 12/02/17.
 */

@Named
@ViewScoped
public class EcdeListMB implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Inject
    EcdeService ecdeService;

    String opno;

    LazyDataModel<Ecde> patients;

    Filter<Ecde> filter = new Filter<>(new Ecde());

    List<Ecde> selectedPatients; //patients selected in checkbox column

    List<Ecde> filteredValue;// datatable filteredValue attribute (column filters)

    @PostConstruct
    public void initDataModel() {
        patients = new LazyDataModel<Ecde>() {
            @Override
            public List<Ecde> load(int first, int pageSize,
                    String sortField, SortOrder sortOrder,
                    Map<String, Object> filters) {
                com.github.adminfaces.starter.infra.model.SortOrder order = null;
                if (sortOrder != null) {
                    order = sortOrder.equals(SortOrder.ASCENDING) ? com.github.adminfaces.starter.infra.model.SortOrder.ASCENDING
                            : sortOrder.equals(SortOrder.DESCENDING) ? com.github.adminfaces.starter.infra.model.SortOrder.DESCENDING
                            : com.github.adminfaces.starter.infra.model.SortOrder.UNSORTED;
                }
                filter.setFirst(first).setPageSize(pageSize)
                        .setSortField(sortField).setSortOrder(order)
                        .setParams(filters);
                List<Ecde> list = ecdeService.paginate(filter);
                setRowCount((int) ecdeService.count(filter));
                return list;
            }

            @Override
            public int getRowCount() {
                return super.getRowCount();
            }

            @Override
            public Ecde getRowData(String key) {
                return ecdeService.findByOpno(key);
            }
        };
    }

    public void clear() {
        filter = new Filter<Ecde>(new Ecde());
    }

    public List<String> completeModelOpno(String query) {
        List<String> result = ecdeService.getOpno(query);
        return result;
    }

    public List<String> completeModel(String query) {
        List<String> result = ecdeService.getNames(query);
        return result;
    }

       
    public void findPatientById(String opno) {
        if (opno == null) {
            throw new BusinessException("Provide ECDE ID to load");
        }
        selectedPatients.add(ecdeService.findByOpno(opno));
    }

    public void delete() {
        int numPatients = 0;
        for (Ecde selectedCar : selectedPatients) {
            numPatients++;
            ecdeService.remove(selectedCar);
        }
        selectedPatients.clear();
        addDetailMessage(numPatients + " ECDE deleted successfully!");
    }

    public List<Ecde> getFilteredValue() {
        return filteredValue;
    }

    public void setFilteredValue(List<Ecde> filteredValue) {
        this.filteredValue = filteredValue;
    }

    public LazyDataModel<Ecde> getPatients() {
        return patients;
    }

    public void setPatients(LazyDataModel<Ecde> patients) {
        this.patients = patients;
    }

    public Filter<Ecde> getFilter() {
        return filter;
    }

    public void setFilter(Filter<Ecde> filter) {
        this.filter = filter;
    }

    public List<Ecde> getSelectedPatients() {
        return selectedPatients;
    }

    public void setSelectedPatients(List<Ecde> selectedPatients) {
        this.selectedPatients = selectedPatients;
    }

    public String getOpno() {
        return opno;
    }

    public void setOpno(String opno) {
        this.opno = opno;
    }
}
