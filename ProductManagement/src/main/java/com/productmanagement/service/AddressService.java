package com.productmanagement.service;

import com.productmanagement.dto.AddressRequest;
import com.productmanagement.dto.AddressResponse;
import com.productmanagement.entity.Address;
import com.productmanagement.entity.User;
import com.productmanagement.exception.ResourceNotFoundException;
import com.productmanagement.repository.AddressRepository;
import com.productmanagement.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    public AddressService(AddressRepository addressRepository, UserRepository userRepository) {
        this.addressRepository = addressRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public AddressResponse addAddress(Long userId, AddressRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        Address address = new Address();
        address.setUser(user);
        address.setStreet(request.getStreet());
        address.setCity(request.getCity());
        address.setState(request.getState());
        address.setZipCode(request.getZipCode());
        address.setCountry(request.getCountry());

        address = addressRepository.save(address);
        return convertToResponse(address);
    }

    public List<AddressResponse> getUserAddresses(Long userId) {
        return addressRepository.findByUserId(userId).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public AddressResponse getAddressById(Long addressId, Long userId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found with id: " + addressId));

        if (!address.getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException("Address not found with id: " + addressId);
        }

        return convertToResponse(address);
    }

    @Transactional
    public AddressResponse updateAddress(Long addressId, Long userId, AddressRequest request) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found with id: " + addressId));

        if (!address.getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException("Address not found with id: " + addressId);
        }

        address.setStreet(request.getStreet());
        address.setCity(request.getCity());
        address.setState(request.getState());
        address.setZipCode(request.getZipCode());
        address.setCountry(request.getCountry());

        address = addressRepository.save(address);
        return convertToResponse(address);
    }

    @Transactional
    public void deleteAddress(Long addressId, Long userId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found with id: " + addressId));

        if (!address.getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException("Address not found with id: " + addressId);
        }

        addressRepository.delete(address);
    }

    private AddressResponse convertToResponse(Address address) {
        AddressResponse response = new AddressResponse();
        response.setId(address.getId());
        response.setStreet(address.getStreet());
        response.setCity(address.getCity());
        response.setState(address.getState());
        response.setZipCode(address.getZipCode());
        response.setCountry(address.getCountry());
        return response;
    }
}

