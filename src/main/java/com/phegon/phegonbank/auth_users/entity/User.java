package com.phegon.phegonbank.auth_users.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@Table(name="users")
@AllArgsConstructor
@NoArgsConstructor
public class User {

}
