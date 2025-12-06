package com.example.car.services;

import com.example.car.entities.Car;
import com.example.car.models.CarResponse;
import com.example.car.models.Client;
import com.example.car.repositories.CarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CarService {

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private RestTemplate restTemplate;

    /**
     * URL du service client via Eureka (nom du service)
     * Avec @LoadBalanced, RestTemplate peut résoudre le nom du service via Eureka
     * Format: http://SERVICE-NAME/path
     */
    private static final String CLIENT_SERVICE_URL =
            "http://SERVICE-CLIENT/api/client/";

    /**
     * Récupère toutes les voitures avec les détails des clients
     */
    public List<CarResponse> findAll() {
        List<Car> cars = carRepository.findAll();

        return cars.stream()
                .map(this::mapToCarResponse)
                .collect(Collectors.toList());
    }

    /**
     * Récupère une voiture par son ID avec les détails du client
     */
    public CarResponse findById(Long id) throws Exception {
        Car car = carRepository.findById(id)
                .orElseThrow(() -> new Exception("Voiture non trouvée avec l'ID: " + id));

        return mapToCarResponse(car);
    }

    /**
     * Convertit une entité Car en CarResponse en récupérant les détails du client
     */
    private CarResponse mapToCarResponse(Car car) {
        // Récupération du client depuis le service client
        Client client = null;
        try {
            client = restTemplate.getForObject(
                    CLIENT_SERVICE_URL + car.getClientId(),
                    Client.class);
        } catch (Exception e) {
            // En cas d'erreur, on continue avec un client null
            System.err.println("Erreur lors de la récupération du client: " + e.getMessage());
        }

        // Construction de la réponse
        return CarResponse.builder()
                .id(car.getId())
                .brand(car.getBrand())
                .model(car.getModel())
                .matricule(car.getMatricule())
                .client(client)
                .build();
    }
}