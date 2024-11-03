package ru.nntu.Git.frontend;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyNotifier;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.Lumo;
import org.apache.hc.core5.http.ParseException;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

@Route("")
@PageTitle("Start")
public class Frontend extends VerticalLayout implements KeyNotifier {
    private final TextField input = new TextField("", "Type here...");
    private final Button inputBtn = new Button("Save", VaadinIcon.CHECK.create());
    private final Button viewBtn = new Button("Show");
    private TextArea textArea = new TextArea();

    @Autowired
    private final BackendService backendService;

    public Frontend(BackendService backendService) {
        this.backendService = backendService;

        HorizontalLayout upper_layout = new HorizontalLayout();
        HorizontalLayout lower_layout = new HorizontalLayout();

        upper_layout.setHeight("100%");
        upper_layout.setAlignItems(Alignment.CENTER);

        lower_layout.setHeight("100%");
        lower_layout.setAlignItems(Alignment.CENTER);

        inputBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
        inputBtn.addClickListener(e -> {
            try {
                backendService.write(input.getValue());
                Notification.show("Text written to backend");
            } catch (IOException ex) {
                Notification.show("Error writing to backend");
            }
            input.setValue("");
        });
        viewBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_CONTRAST);
        viewBtn.addClickListener(e -> {
            try {
                textArea.setValue(backendService.read());
            } catch (IOException | ParseException ex) {
                Notification.show("Error reading from backend");
            }
            input.setValue("");
        });

        input.setValueChangeMode(ValueChangeMode.EAGER);
        input.addValueChangeListener(e -> {
            if (e.getValue() == null || e.getValue().trim().isEmpty()) {
                inputBtn.setEnabled(false);
            }
            else {
                inputBtn.setEnabled(true);
                input.addKeyDownListener(Key.ENTER, ev -> inputBtn.click());
            }
        });
        inputBtn.setEnabled(false);

        textArea.setWidth("300px");
        textArea.setLabel("Output");
        textArea.setValue("");
        textArea.setReadOnly(true);

        upper_layout.add(input, inputBtn, viewBtn);
        lower_layout.add(textArea);

        add(upper_layout, lower_layout);
        setHeight("100%");
        setAlignItems(Alignment.CENTER);
        getElement().setAttribute("theme", Lumo.DARK);
    }
}

