package com.github.adminfaces.starter.bean;

import com.github.adminfaces.starter.infra.model.Filter;
import com.github.adminfaces.starter.model.User;
import com.github.adminfaces.starter.service.UserService;
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
public class UsersListMB implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Inject
    UserService userService;

    String opno;

    LazyDataModel<User> patients;

    Filter<User> filter = new Filter<>(new User());

    List<User> selectedPatients; //patients selected in checkbox column

    List<User> filteredValue;// datatable filteredValue attribute (column filters)

    @PostConstruct
    public void initDataModel() {
        patients = new LazyDataModel<User>() {
            @Override
            public List<User> load(int first, int pageSize,
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
                List<User> list = userService.paginate(filter);
                setRowCount((int) userService.count(filter));
                return list;
            }

            @Override
            public int getRowCount() {
                return super.getRowCount();
            }

            @Override
            public User getRowData(String key) {
                return userService.findByOpno(key);
            }
        };
    }

    public void clear() {
        filter = new Filter<User>(new User());
    }

    public List<String> completeModelOpno(String query) {
        List<String> result = userService.getOpno(query);
        return result;
    }

    public List<String> completeModel(String query) {
        List<String> result = userService.getNames(query);
        return result;
    }

    public List<String> completeModelUserLevel(String query) {
        List<String> result = userService.getUserLevels(query);
        return result;
    }
    
    public void findPatientById(String opno) {
        if (opno == null) {
            throw new BusinessException("Provide User Account Type ID to load");
        }
        selectedPatients.add(userService.findByOpno(opno));
    }

    public void delete() {
        int numPatients = 0;
        for (User selectedCar : selectedPatients) {
            numPatients++;
            userService.remove(selectedCar);
        }
        selectedPatients.clear();
        addDetailMessage(numPatients + " User Account Type deleted successfully!");
    }

    public List<User> getFilteredValue() {
        return filteredValue;
    }

    public void setFilteredValue(List<User> filteredValue) {
        this.filteredValue = filteredValue;
    }

    public LazyDataModel<User> getPatients() {
        return patients;
    }

    public void setPatients(LazyDataModel<User> patients) {
        this.patients = patients;
    }

    public Filter<User> getFilter() {
        return filter;
    }

    public void setFilter(Filter<User> filter) {
        this.filter = filter;
    }

    public List<User> getSelectedPatients() {
        return selectedPatients;
    }

    public void setSelectedPatients(List<User> selectedPatients) {
        this.selectedPatients = selectedPatients;
    }

    public String getOpno() {
        return opno;
    }

    public void setOpno(String opno) {
        this.opno = opno;
    }
}
