// AngleFinder_02.cpp : Defines the entry point for the console application.
/*  
	I wrote this program for testing in a game called War Thunder. It is meant to support the
	proposal of artillery tanks as an expansion to the game.
	
	This program requires the user to input appropriate parameters in order to find out the proper 
	angle of fire to hit a target.
*/

//https://wikimedia.org/api/rest_v1/media/math/render/svg/4db61cb4c3140b763d9480e51f90050967288397
//Source formula ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
#include"stdafx.h"
#include <math.h>
#include <iostream>
#include <string>
#define PI 3.14159265
#define g 9.80665

float GetProjectileVelocity();
float GetTargetDistance();
float GetMyHeight();
float GetTargetHeight();
double CalculateFiringAngle(double velocity, double distance, double height, int plusMinus);
bool InquireRecalculation();

int main()
{
	bool recalculate;

	do
	{
		float projectileVelocity = GetProjectileVelocity();
		float targetDistance = GetTargetDistance();
		float myHeight = GetMyHeight();
		float targetHeight = GetTargetHeight();
		float heightDiff = targetHeight - myHeight;

		double firingAngleA = CalculateFiringAngle(projectileVelocity, targetDistance, heightDiff, -1);
		double firingAngleB = CalculateFiringAngle(projectileVelocity, targetDistance, heightDiff, 1);

		/*
		adjust firing angles according to the angle of the tank.
		double tankAngle = 45;
		firingAngleA -= tankAngle;
		firingAngleB -= tankAngle;
		*/

		std::cout << "Firing angleA is: " << firingAngleA << " degrees.\n";
		std::cout << "Firing angleB is: " << firingAngleB << " degrees.\n";
		recalculate = InquireRecalculation();
	} while (recalculate == true);

	return 0;
}

float GetProjectileVelocity()
{
	std::cout << "Please enter projectile velocity. (Meters/Second): ";
	std::string velocity;
	std::getline(std::cin, velocity);
	return strtof((velocity).c_str(), 0);
}

float GetTargetDistance()
{
	std::cout << "How far away is the target? (Meters): ";
	std::string distance;
	std::getline(std::cin, distance);
	return strtof((distance).c_str(), 0);
}

float GetMyHeight()
{
	std::cout << "How high up are you off the ground? (Meters): ";
	std::string height;
	std::getline(std::cin, height);
	return strtof((height).c_str(), 0);
}

float GetTargetHeight()
{
	std::cout << "How high up is the target off the ground? (Meters): ";
	std::string height;
	std::getline(std::cin, height);
	return strtof((height).c_str(), 0);
}

double CalculateFiringAngle(double velocity, double distance, double height, int plusMinus)
{
	double distanceSqrd = distance * distance;
	double velocitySqrd = velocity * velocity;
	double velocityToFourth = velocity * velocity * velocity * velocity;

	double numerator = (velocityToFourth - g * ((g * distanceSqrd) + (2 * height * velocitySqrd)));
	numerator = sqrt(numerator);
	numerator = velocitySqrd + (plusMinus * numerator);

	double denominator = g * distance;
	double angle = atan2(numerator, denominator) * (180 / PI);
	return angle;
}

bool InquireRecalculation()
{
	bool inputError;
	bool recalculate;

	do
	{
		std::cout << "\nNew calculation?\n";
		std::cout << "y/n or 1/0: ";
		std::string response;
		inputError = false;
		std::getline(std::cin, response);
		if (response[0] == '1' || response[0] == 'y' || response[0] == 'Y')
		{
			recalculate = true;
		}
		else if (response[0] == '0' || response[0] == 'n' || response[0] == 'N')
		{
			recalculate = false;
		}
		else
		{
			inputError = true;
			std::cout << "\nInput was invalid. Please try again...\n";
		}
	} while (inputError != false);

	std::cout << "\n";
	return recalculate;
}