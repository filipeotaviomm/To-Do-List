package com.todolist.todo.services;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.todolist.todo.dtos.CreateAddressRequestDto;
import com.todolist.todo.dtos.GetAddressResponseDto;
import com.todolist.todo.exceptions.BadRequestException;
import com.todolist.todo.exceptions.PermissionDeniedException;
import com.todolist.todo.models.AddressModel;
import com.todolist.todo.models.UserModel;
import com.todolist.todo.models.UserModel.UserRole;
import com.todolist.todo.repositories.AddressRepository;
import com.todolist.todo.repositories.UserRepository;
import com.todolist.todo.utils.TokenUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AddressService {
  private final UserRepository userRepository;
  private final AddressRepository addressRepository;
  private final ModelMapper modelMapper = new ModelMapper();
  private final TokenUtil tokenUtil;

  public GetAddressResponseDto createAddress(CreateAddressRequestDto body, String token) {

    AddressModel address = new AddressModel();
    BeanUtils.copyProperties(body, address);

    UserModel userLogged = tokenUtil.getUserFromToken(token);
    userLogged.setAddress(address);

    addressRepository.save(address);
    userRepository.save(userLogged);

    return modelMapper.map(address, GetAddressResponseDto.class);
  }

  public GetAddressResponseDto getAddress(String token) {

    UserModel userLogged = tokenUtil.getUserFromToken(token);

    return modelMapper.map(userLogged.getAddress(), GetAddressResponseDto.class);
  }

  public List<GetAddressResponseDto> getAllAddresses(String token) {

    UserModel userLogged = tokenUtil.getUserFromToken(token);
    if (userLogged.getRole().equals(UserModel.UserRole.USER)) {
      throw new PermissionDeniedException("You don't have permission to access it");
    }

    var allAddresses = addressRepository.findAll();

    return allAddresses.stream()
        .map(address -> modelMapper.map(address, GetAddressResponseDto.class))
        .collect(Collectors.toList());

  }

  public GetAddressResponseDto updateAdress(
      Long id, CreateAddressRequestDto body, String token) {

    Optional<AddressModel> foundAddress = addressRepository.findById(id);
    if (foundAddress.isEmpty()) {
      throw new BadRequestException("This address don't exist");
    }

    UserModel userLogged = tokenUtil.getUserFromToken(token);
    if (userLogged.getAddress().getId() != id && userLogged.getRole() == UserRole.USER) {
      throw new PermissionDeniedException("You don't have permission to do it.");
    }

    var address = foundAddress.get();
    BeanUtils.copyProperties(body, address);
    addressRepository.save(address);

    return modelMapper.map(address, GetAddressResponseDto.class);
  }

  public void deleteAddress(Long id, String token) {
    Optional<AddressModel> foundAddress = addressRepository.findById(id);
    if (foundAddress.isEmpty()) {
      throw new BadRequestException("This address doesn't exist");
    }

    UserModel userLogged = tokenUtil.getUserFromToken(token);
    if (userLogged.getAddress().getId() != id && userLogged.getRole() == UserRole.USER) {
      throw new PermissionDeniedException("You don't have permission to do it.");
    }

    Optional<UserModel> user = userRepository.findByAddress(foundAddress.get());
    if (user.isPresent()) {
      user.get().setAddress(null);
      userRepository.save(user.get());
    }

    addressRepository.delete(foundAddress.get());
  }
}
