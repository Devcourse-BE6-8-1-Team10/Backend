package com.back.domain.member.address.Service;

import com.back.domain.member.address.entity.Address;
import com.back.domain.member.address.repository.AddressRepository;
import com.back.domain.member.member.entity.Member;
import com.back.global.exception.ServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AddressService {
    private final AddressRepository addressRepository;

    public Address submitAddress(Member member, String content) {
        addressRepository.findByMemberAndContent(member, content)
                .ifPresent(existingAddress -> {
                    throw new ServiceException(409, "이미 동일한 주소가 존재합니다.");
                });

        Address address = new Address(content, false, member);

        return addressRepository.save(address);
    }
}
