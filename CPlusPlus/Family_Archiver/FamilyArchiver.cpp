// FamilyArchiver.cpp : Defines the entry point for the console application.
/*
This is an experiment to learn how to serialize classes of information to a file using the boost library.
This program allows you to Create, Read, Update, and Delete families and members of families.
*/
#include "stdafx.h"
#include <stdio.h>  // defines FILENAME_MAX
#include <direct.h>
#include <windows.h>
#include <cstdlib>
#include <iostream>
#include <io.h>
#include <fstream>
#include <list>
#include <string>

#include <boost/lexical_cast.hpp>
#include <boost/archive/binary_oarchive.hpp>
#include <boost/archive/binary_iarchive.hpp>
#include <boost/serialization/list.hpp>

using namespace boost::archive;

//FamilyInfo
class FamilyInfo
{
public:
	std::string familyName;
	std::list<std::string> familyMembersList;
};

template <typename Archive>
void serialize(Archive &familyInfoArchive, FamilyInfo &familyInfo, const unsigned int version)
{
	familyInfoArchive & familyInfo.familyName;
	familyInfoArchive & familyInfo.familyMembersList;
}
//End FamilyInfo

//FamilyCollection
class FamilyCollection
{
public:
	std::list<FamilyInfo> familyList;

}familyCollection;

template <typename Archive>
void serialize(Archive &familyCollectionArchive, FamilyCollection &family, const unsigned int version)
{
	familyCollectionArchive & family.familyList;
}
//End FamilyCollection



void DisplayFamilyActions();
std::string CreateFamily(std::string &question, std::string &prompt, std::string &familyInput);
void DisplayFamilies();
void EditFamily(FamilyInfo* &familyPointer, std::string commandInput, std::string familyInput, std::string question, std::string prompt, int commandID, bool repeat);
bool RemoveFamily(std::string &familyName);
FamilyInfo* SelectFamily(std::string &familyName);

void DisplayMemberActions();
void DisplayFamily(FamilyInfo* &familyPointer);
bool ChangeFamilyName(FamilyInfo* &familyPointer, std::string &newFamilyName);
void AddFamilyMember(FamilyInfo* &familyPointer, std::string question, std::string prompt);
bool RemoveFamilyMember(FamilyInfo* &familyPointer, std::string &memberName);
bool ChangeMemberName(FamilyInfo* &familyPointer, std::string &oldMemberName, std::string &newMemberName);

void CreateDataFile();
void SaveData();
void LoadData();
std::string GetCurrentWindowsDirectory();
void CheckDirectory();
inline bool FileCheck(const std::string& fileName);
bool FamilyNameCompare(const FamilyInfo &a, const FamilyInfo &b);
bool IsNumber(std::string &stringValue);
void removeSpaces(std::string &input);

void PromptAction(std::string &question, std::string &commandInput, int &commandID);
void PromptFamilyName(std::string &question, std::string &prompt, std::string &familyInput);
void PromptMemberName(std::string &question, std::string &prompt, std::string &memberInput);

std::string directoryPath;


char* newDirectoryPointer;


int main()
{
	directoryPath = GetCurrentWindowsDirectory() + "\\Family_Data";
	newDirectoryPointer = new char[directoryPath.length() + 1];
	std::strcpy(newDirectoryPointer, directoryPath.c_str());
	directoryPath = directoryPath + "\\Family.sav";
	//std::cout << directoryPath;

	LoadData();

	std::cout << "Welcome to the family archiver.\n";

	std::string commandInput;
	std::string familyInput;
	std::string question;
	std::string prompt;
	int commandID = 0;
	bool repeat = true;

	do
	{
		DisplayFamilyActions();

		question = "What action would you like to take?\n";
		PromptAction(question, commandInput, commandID);

		switch (commandID)
		{
			case 0:
			{
				repeat = false;
				break;
			}
			case 1:
			{
				std::string familyName = CreateFamily(question, prompt, familyInput);

				if (familyName != "0")
				{
					FamilyInfo* familyPointer = SelectFamily(familyName);
					AddFamilyMember(familyPointer, question, prompt);
				}
				break;
			}
			case 2:
			{
			retryFamilyView:
				DisplayFamilies();
				question = "\nWhich family do you want to view in the archive?\n";
				prompt = "Enter name of the family or type 0 to quit.\n";
				PromptFamilyName(question, prompt, familyInput);

				if (familyInput != "0")
				{
					FamilyInfo* familyPointer = SelectFamily(familyInput);

					if (familyPointer != NULL)
					{
						DisplayFamily(familyPointer);
					}
					else
					{
						std::cout << "Family not found. Try again?\n";
						goto retryFamilyView;
					}
				}
				break;
			}
			case 3:
			{
			retryFamilyEdit:
				DisplayFamilies();
				question = "Which family do you want to edit in the archive?\n";
				prompt = "Enter name of the family or type 0 to quit.\n";
				PromptFamilyName(question, prompt, familyInput);

				if (familyInput != "0")
				{
					FamilyInfo* familyPointer = SelectFamily(familyInput);
					if (familyPointer != NULL)
					{
						EditFamily(familyPointer, commandInput, familyInput, question, prompt, commandID, repeat);
					}
					else
					{
						std::cout << "Family not found. Try again?\n";
						goto retryFamilyEdit;
					}
				}
				break;
			}
			case 4:
			{
			retryFamilyRemove:
				DisplayFamilies();
				question = "\nWhich family do you want to remove from the archive?\n";
				prompt = "Enter name of the family or type 0 to quit.\n";
				PromptFamilyName(question, prompt, familyInput);

				if (familyInput != "0")
				{
					if (RemoveFamily(familyInput))
					{
						std::cout << "The " + familyInput + " family has successfully been removed from the archive.\n";
						SaveData();
					}
					else
					{
						std::cout << "Family not found. Try again?\n\n";
						goto retryFamilyRemove;
					}
				}
				break;
			}
			default:
			{
				std::cout << "\nInvalid input. Please enter the appropriate number.\n";
			}
		}
	} while (repeat);

	return 0;
}



void DisplayFamilyActions()
{
	std::cout << std::endl;
	std::cout << "What action would you like to take?\n";
	std::cout << "0: Exit family archiver.\n";
	std::cout << "1: Create a new family.\n";
	std::cout << "2: View a family.\n";
	std::cout << "3: Edit a family.\n";
	std::cout << "4: Remove a family.\n";
	std::cout << "\nActivate a command using the appropriate number.\n";
	std::cout << "Value: ";
}

std::string CreateFamily(std::string &question, std::string &prompt, std::string &familyInput)
{
	DisplayFamilies();
	FamilyInfo familyInfo;
	question = "What is the last name of the family you would like to create?\n";
	prompt = "Enter last name or type 0 to quit.\n";

retryLastName:
	PromptFamilyName(question, prompt, familyInput);

	if (familyInput != "0")
	{
		FamilyInfo* familyPointer = NULL;
		familyPointer = SelectFamily(familyInput);

		if (familyPointer == NULL)
		{
			familyInfo.familyName = familyInput;
			familyCollection.familyList.push_front(familyInfo);
			familyCollection.familyList.sort(FamilyNameCompare);
			SaveData();
			return familyInput;
		}
		else
		{
			std::cout << "Name already in use.\n";
			goto retryLastName;
		}
	}
	else
	{
		return "0";
	}
}

void DisplayFamilies()
{
	if (familyCollection.familyList.size() > 0)
	{
		std::cout << "Here are the current archived families.\n";
		for (FamilyInfo & familyInfo : familyCollection.familyList)
		{
			std::cout << "Family name: " << familyInfo.familyName << "\n";
		}
		std::cout << std::endl;
	}
	else
	{
		std::cout << "There are no families added yet for viewing.\n";
	}
}

void EditFamily(FamilyInfo* &familyPointer, std::string commandInput, std::string familyInput, std::string question, std::string prompt, int commandID, bool repeat)
{
	std::cout << "Welcome to the " << familyPointer->familyName << " archive.\n\n";
	std::string memberInput;
	do
	{
		DisplayMemberActions();

		question = "What action would you like to take?\n";
		PromptAction(question, commandInput, commandID);

		switch (commandID)
		{
			case 0:
			{
				repeat = false;
				break;
			}
			case 1:
			{
				DisplayFamily(familyPointer);
				std::cout << std::endl;
				break;
			}
			case 2:
			{
			retryFamilyNameChange:
				DisplayFamilies();
				question = "What name shall the family use?\n";
				prompt = "Enter new family name or type 0 to quit.\n";
				PromptFamilyName(question, prompt, familyInput);
				if (familyInput != "0")
				{
					bool nameChanged = ChangeFamilyName(familyPointer, familyInput);
					if (!nameChanged)
					{
						std::cout << "\nThat name is already added.\n";
						goto retryFamilyNameChange;
					}
				}
				break;
			}
			case 3:
			{
				AddFamilyMember(familyPointer, question, prompt);
				break;
			}
			case 4:
			{
			retryMemberRename:
				DisplayFamily(familyPointer);
				question = "Which family member do you want to edit?\n";
				prompt = "Enter member name or type 0 to quit.\n";
				PromptMemberName(question, prompt, memberInput);

				if (memberInput != "0")
				{
					bool nameExists = false;
					for (std::string memberName : familyPointer->familyMembersList)
					{
						if (memberInput == memberName)
						{
							nameExists = true;
						}
					}
					if (nameExists)
					{
						question = "What name would you like to change this member to?\n";
						prompt = "Enter member name or type 0 to quit.\n";
						std::string oldName = memberInput;
						PromptMemberName(question, prompt, memberInput);

						if (memberInput != "0")
						{
							if (memberInput != oldName)
							{
								bool nameChanged = ChangeMemberName(familyPointer, oldName, memberInput);

								if (!nameChanged)
								{
									goto retryMemberRename;
								}
							}
							else
							{
								std::cout << "\nThe name you are trying to change to is already added.\n";
								goto retryMemberRename;
							}
						}
					}
					else
					{
						std::cout << "\nThe name you are trying to change from doesn't exist.\n";
						goto retryMemberRename;
					}
				}
				break;
			}
			case 5:
			{
			retryMemberRemove:
				DisplayFamily(familyPointer);
				question = "Which family member do you wish to remove?\n";
				prompt = "Enter member name or type 0 to quit.\n";
				PromptMemberName(question, prompt, memberInput);
				if (memberInput != "0")
				{
					bool memberRemoved = RemoveFamilyMember(familyPointer, memberInput);
					if (!memberRemoved)
					{
						std::cout << "Family member not found.\n";
						goto retryMemberRemove;
					}
				}
				break;
			}
			default:
			{
				std::cout << "\nInvalid input. Please enter the appropriate number.\n";
			}
		}
	} while (repeat);
}

bool RemoveFamily(std::string &familyName)
{
	int listSize = familyCollection.familyList.size();

	familyCollection.familyList.remove_if([familyName](FamilyInfo familyInfo) { return familyInfo.familyName == familyName; });

	if (listSize != familyCollection.familyList.size())
	{
		return true;
	}
	else
	{
		return false;
	}
}

FamilyInfo* SelectFamily(std::string &familyName)
{
	std::list<FamilyInfo>::iterator familyIterator =
		std::find_if(familyCollection.familyList.begin(), familyCollection.familyList.end(),
			[familyName](const FamilyInfo & familyInfo) -> bool { return familyInfo.familyName == familyName; });

	FamilyInfo* familyPointer;

	if (familyIterator != familyCollection.familyList.end())
	{
		familyPointer = &*familyIterator;
	}
	else
	{
		familyPointer = NULL;
	}

	return familyPointer;
}



void DisplayMemberActions()
{
	std::cout << "What action would you like to take?\n";
	std::cout << "0: Exit family editor.\n";
	std::cout << "1: Show family members.\n";
	std::cout << "2: Edit family name.\n";
	std::cout << "3: Add member(s).\n";
	std::cout << "4: Edit a Member.\n";
	std::cout << "5: Remove a Member.\n";
	std::cout << "\nActivate a command using the appropriate number.\n";
	std::cout << "Value: ";
}

void DisplayFamily(FamilyInfo* &familyPointer)
{
	if (familyPointer->familyMembersList.size() > 0)
	{
		std::cout << "Here are the current archived family members.\n";
		int familyCounter = 1;
		for (std::string memberName : familyPointer->familyMembersList)
		{
			std::cout << "Family member " << familyCounter << ": " << memberName << "\n";
			familyCounter++;
		}
	}
	else
	{
		std::cout << "There are no members added yet for viewing.\n";
	}
}

bool ChangeFamilyName(FamilyInfo* &familyPointer, std::string &newFamilyName)
{
	FamilyInfo* familyCheckPointer = NULL;
	familyCheckPointer = SelectFamily(newFamilyName);

	if (familyCheckPointer == NULL)
	{
		if (familyPointer->familyName != newFamilyName)
		{
			std::string oldFamilyName = familyPointer->familyName;
			familyPointer->familyName = newFamilyName;
			std::cout << "\nChanged family name from: " << oldFamilyName << " to: " << newFamilyName << ".\n\n";
			SaveData();
			return true;
		}
		else
		{
			return false;
		}
	}
	else
	{
		return false;
	}
}

void AddFamilyMember(FamilyInfo* &familyPointer, std::string question, std::string prompt)
{
	std::string memberInput;
	DisplayFamily(familyPointer);
	question = "\nWhat is the name of the member you would like to add to this family?\n";
	prompt = "Enter name or type 0 to quit.\n";

	bool repeat = true;
	do
	{
		PromptMemberName(question, prompt, memberInput);

		if (memberInput != "0")
		{
			bool oldNameArchived = false;

			for (std::string memberName : familyPointer->familyMembersList)
			{
				if (memberInput == memberName)
				{
					oldNameArchived = true;
				}
			}
			if (!oldNameArchived)
			{
				familyPointer->familyMembersList.push_front(memberInput);
				familyPointer->familyMembersList.sort();
				SaveData();
				std::cout << "Name entered...\n";
				std::cout << "Here are the current members of this family.\n";
				int familyCounter = 1;
				for (std::string & familyMember : familyPointer->familyMembersList)
				{
					std::cout << "Family member " << familyCounter << ": " << familyMember << "\n";
					familyCounter++;
				}
			}
			else
			{
				std::cout << "\nThe name you're trying to add is already in use.\n";
			}
		}
		else
		{
			repeat = false;
		}
	} while (repeat);
}

bool RemoveFamilyMember(FamilyInfo* &familyPointer, std::string &memberName)
{
	int listSize = familyPointer->familyMembersList.size();

	familyPointer->familyMembersList.remove(memberName);

	if (listSize != familyPointer->familyMembersList.size())
	{
		SaveData();
		return true;
	}
	else
	{
		return false;
	}

	return true;
}

bool ChangeMemberName(FamilyInfo* &familyPointer, std::string &oldMemberName, std::string &newMemberName)
{
	bool vacantName = true;

	for (std::string memberName : familyPointer->familyMembersList)
	{
		if (newMemberName == memberName)
		{
			vacantName = false;
		}
	}
	if (vacantName)
	{
		familyPointer->familyMembersList.remove(oldMemberName);
		familyPointer->familyMembersList.push_front(newMemberName);
		familyPointer->familyMembersList.sort();
		SaveData();
		std::cout << oldMemberName << " has changed to " << newMemberName << ".\n";
		return true;
	}
	else
	{
		std::cout << "\nThe name you are trying to change to is already added.\n";
		return false;
	}
}



void CreateDataFile()
{
	std::ofstream file{ directoryPath, std::ofstream::binary };
	binary_oarchive boa{ file };
	boa << familyCollection;
}

void SaveData()
{
	CheckDirectory();
	std::ofstream file{ directoryPath, std::ofstream::binary };
	binary_oarchive boa{ file };
	boa << familyCollection;
}

void LoadData()
{
	CheckDirectory();
	std::ifstream file{ directoryPath, std::ofstream::binary };
	binary_iarchive bia{ file };
	bia >> familyCollection;
}

std::string GetCurrentWindowsDirectory()
{
	const unsigned long maxDir = 260;
	char currentDir[maxDir];
	GetCurrentDirectory(maxDir, currentDir);
	return std::string(currentDir);
}

void CheckDirectory()
{
	if (CreateDirectory(newDirectoryPointer, NULL))
	{
		CreateDataFile();
	}
	else
	{
		if (!FileCheck(directoryPath))
		{
			CreateDataFile();
		}
	}
}

inline bool FileCheck(const std::string& fileName)
{
	std::ifstream file(fileName.c_str());
	return file.good();
}

bool FamilyNameCompare(const FamilyInfo &a, const FamilyInfo &b)
{
	return a.familyName < b.familyName;
}

bool IsNumber(std::string &stringValue)
{
	bool hitDecimal = false;
	for (char charCounter : stringValue)
	{
		if (charCounter == '.' && !hitDecimal)
		{
			hitDecimal = true;
		}
		else if (!isdigit(charCounter))
		{
			return false;
		}
	}
	return true;
}

void removeSpaces(std::string &input)
{
	input.erase(std::remove(input.begin(), input.end(), ' '), input.end());
}



void PromptAction(std::string &question, std::string &commandInput, int &commandID)
{
	std::getline(std::cin, commandInput);
	std::cout << std::endl;

	removeSpaces(commandInput);
	if (IsNumber(commandInput) && commandInput != "")
	{
		commandID = boost::lexical_cast<int>(commandInput);
	}
	else
	{
		std::cout << "\nInvalid input. Please enter the appropriate number.\n";
	}
}

void PromptFamilyName(std::string &question, std::string &prompt, std::string &familyInput)
{
retryFamilyInput:
	std::cout << question;
	std::cout << prompt;
	std::getline(std::cin, familyInput);
	removeSpaces(familyInput);
	if (familyInput == "")
	{
		std::cout << "Nothing was input.\n";
		goto retryFamilyInput;
	}
}

void PromptMemberName(std::string &question, std::string &prompt, std::string &memberInput)
{
retryMemberInput:
	std::cout << question;
	std::cout << prompt;
	std::getline(std::cin, memberInput);
	removeSpaces(memberInput);
	if (memberInput == "")
	{
		std::cout << "Nothing was input.\n";
		goto retryMemberInput;
	}
}